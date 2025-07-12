package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.dto.auth.AuthResponse;
import iuh.fit.se.shortenlink.dto.auth.LoginRequest;
import iuh.fit.se.shortenlink.dto.auth.RefreshRequest;
import iuh.fit.se.shortenlink.dto.auth.RegisterRequest;
import iuh.fit.se.shortenlink.dto.auth.MeResponse;
import iuh.fit.se.shortenlink.entity.User;
import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import iuh.fit.se.shortenlink.repository.UserRepository;
import iuh.fit.se.shortenlink.security.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenStore refreshTokenStore;
    private final iuh.fit.se.shortenlink.integration.s3.S3StorageService s3StorageService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       RefreshTokenStore refreshTokenStore,
                       iuh.fit.se.shortenlink.integration.s3.S3StorageService s3StorageService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenStore = refreshTokenStore;
        this.s3StorageService = s3StorageService;
    }

    @Transactional
    public void register(RegisterRequest request) {
        String normalizedEmail = request.getEmail().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new AppException(ErrorCode.USER_ALREADY_EXISTS);
        }

        User user = User.builder()
                .email(normalizedEmail)
                .username(extractDefaultUsername(normalizedEmail))
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail().toLowerCase())
                .orElseThrow(() -> new AppException(ErrorCode.INVALID_CREDENTIALS));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new AppException(ErrorCode.INVALID_CREDENTIALS);
        }

        return issueTokens(user);
    }

    public AuthResponse refresh(RefreshRequest request) {
        Claims claims = jwtService.parseToken(request.getRefreshToken());
        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Invalid refresh token");
        }

        Long userId = claims.get("uid", Number.class).longValue();
        String persisted = refreshTokenStore.get(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!persisted.equals(request.getRefreshToken())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED, "Refresh token revoked");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        return issueTokens(user);
    }

    public MeResponse me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        String imgUrl = null;
        if (user.getImg() != null) {
            imgUrl = s3StorageService.generatePresignedDownloadUrl(user.getImg()).toString();
        }

        return MeResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .username(user.getUsername())
                .img(imgUrl)
                .createdAt(user.getCreatedAt())
                .build();
    }

    private AuthResponse issueTokens(User user) {
        String accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());
        refreshTokenStore.save(user.getId(), refreshToken);
        return AuthResponse.builder().accessToken(accessToken).build();
    }

    private String extractDefaultUsername(String email) {
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }
        return email;
    }   
}
