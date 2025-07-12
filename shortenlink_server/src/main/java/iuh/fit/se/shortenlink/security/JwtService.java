package iuh.fit.se.shortenlink.security;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureException;
import iuh.fit.se.shortenlink.config.JwtProperties;
import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
@Service
public class JwtService {

    private static final Logger log = LoggerFactory.getLogger(JwtService.class);
    private static final String DEFAULT_PRIVATE_KEY_LOCATION = "classpath:keys/private_key.pem";
    private static final String DEFAULT_PUBLIC_KEY_LOCATION = "classpath:keys/public_key.pem";

    private final JwtProperties jwtProperties;
    private final ResourceLoader resourceLoader = new DefaultResourceLoader();
    private PrivateKey privateKey;
    private PublicKey publicKey;
    public JwtService(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }
    public String generateAccessToken(Long userId, String email) {
        return generateToken(userId, email, jwtProperties.getAccessTokenTtlSeconds(), "access");
    }
    public String generateRefreshToken(Long userId, String email) {
        return generateToken(userId, email, jwtProperties.getRefreshTokenTtlSeconds(), "refresh");
    }
    public Claims parseToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(getPublicKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (SignatureException ex) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid token signature");
        } catch (Exception ex) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid or expired token");
        }
    }
    private String generateToken(Long userId, String email, long ttlSeconds, String tokenType) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(email)
                .issuer(jwtProperties.getIssuer())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plusSeconds(ttlSeconds)))
                .claim("uid", userId)
                .claim("type", tokenType)
                .signWith(getPrivateKey(), Jwts.SIG.RS256)
                .compact();
    }
    private synchronized PrivateKey getPrivateKey() {
        if (privateKey == null) {
            privateKey = parsePrivateKey(resolveKeyMaterial(
                    jwtProperties.getPrivateKeyPem(),
                    jwtProperties.getPrivateKeyPath(),
                    DEFAULT_PRIVATE_KEY_LOCATION,
                    "private"
            ));
        }
        return privateKey;
    }
    private synchronized PublicKey getPublicKey() {
        if (publicKey == null) {
            publicKey = parsePublicKey(resolveKeyMaterial(
                    jwtProperties.getPublicKeyPem(),
                    jwtProperties.getPublicKeyPath(),
                    DEFAULT_PUBLIC_KEY_LOCATION,
                    "public"
            ));
        }
        return publicKey;
    }
    private String resolveKeyMaterial(String pemValue, String configuredPath, String defaultPath, String keyType) {
        String normalizedPemValue = normalizePemValue(pemValue);
        if (!normalizedPemValue.isBlank()) {
            if (looksLikeLocation(normalizedPemValue)) {
                log.warn("app.jwt.{}-key-pem contains a resource path; treating it as a key location. Prefer app.jwt.{}-key-path for this.", keyType, keyType);
                return loadTextResource(normalizedPemValue);
            }
            return normalizedPemValue;
        }

        String location = configuredPath != null && !configuredPath.isBlank()
                ? configuredPath.trim()
                : defaultPath;
        return loadTextResource(location);
    }
    private String normalizePemValue(String value) {
        if (value == null) {
            return "";
        }
        return value.trim().replace("\\n", "\n");
    }
    private boolean looksLikeLocation(String value) {
        String normalized = value.trim();
        return normalized.startsWith("classpath:")
                || normalized.startsWith("file:")
                || normalized.startsWith("/")
                || normalized.matches("^[A-Za-z]:[\\\\/].*")
                || (!normalized.contains("-----BEGIN") && normalized.endsWith(".pem"));
    }
    private String loadTextResource(String location) {
        String normalizedLocation = location == null || location.isBlank() ? "" : location.trim();
        log.info("Loading key from location: {}", normalizedLocation);
        try {
            Resource resource = resourceLoader.getResource(normalizedLocation);
            if (!resource.exists() && Path.of(normalizedLocation).toFile().exists()) {
                resource = resourceLoader.getResource("file:" + normalizedLocation);
            }
            try (InputStream is = resource.getInputStream()) {
                return new String(is.readAllBytes(), StandardCharsets.UTF_8);
            }
        } catch (Exception ex) {
            log.error("Failed to load key from '{}': {}", normalizedLocation, ex.getMessage(), ex);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Failed to load key file: " + normalizedLocation);
        }
    }
    private PrivateKey parsePrivateKey(String pem) {
        if (pem == null || pem.isBlank()) {
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Missing JWT private key");
        }
        try {
            String content = pem
                    .replace("-----BEGIN PRIVATE KEY-----", "")
                    .replace("-----END PRIVATE KEY-----", "")
                    .replaceAll("[\\r\\n\\s]+", "");
            log.debug("Private key content length: {}", content.length());
            byte[] bytes = Base64.getDecoder().decode(content);
            PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(bytes);
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to parse private key: {}", ex.getMessage(), ex);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Invalid JWT private key format");
        }
    }
    private PublicKey parsePublicKey(String pem) {
        if (pem == null || pem.isBlank()) {
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Missing JWT public key");
        }
        try {
            String content = pem
                    .replace("-----BEGIN PUBLIC KEY-----", "")
                    .replace("-----END PUBLIC KEY-----", "")
                    .replaceAll("[\\r\\n\\s]+", "");
            log.debug("Public key content length: {}", content.length());
            byte[] bytes = Base64.getDecoder().decode(content);
            X509EncodedKeySpec spec = new X509EncodedKeySpec(bytes);
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (AppException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("Failed to parse public key: {}", ex.getMessage(), ex);
            throw new AppException(ErrorCode.INTERNAL_ERROR, "Invalid JWT public key format");
        }
    }
}
