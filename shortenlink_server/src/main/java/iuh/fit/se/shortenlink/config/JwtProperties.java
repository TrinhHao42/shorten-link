package iuh.fit.se.shortenlink.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String issuer = "shortenlink";
    private long accessTokenTtlSeconds = 900;
    private long refreshTokenTtlSeconds = 604800;
    private String privateKeyPem;
    private String publicKeyPem;
    /** Classpath or file path to private key PEM file (used when privateKeyPem is not set) */
    private String privateKeyPath = "classpath:keys/private_key.pem";
    /** Classpath or file path to public key PEM file (used when publicKeyPem is not set) */
    private String publicKeyPath = "classpath:keys/public_key.pem";
}

