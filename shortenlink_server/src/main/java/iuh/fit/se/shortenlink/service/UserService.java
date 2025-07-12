package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.dto.auth.MeResponse;
import iuh.fit.se.shortenlink.dto.user.UpdateProfileRequest;
import iuh.fit.se.shortenlink.entity.User;
import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import iuh.fit.se.shortenlink.integration.s3.S3StorageService;
import iuh.fit.se.shortenlink.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final S3StorageService s3StorageService;
    private final AuthService authService;

    public UserService(UserRepository userRepository, S3StorageService s3StorageService, AuthService authService) {
        this.userRepository = userRepository;
        this.s3StorageService = s3StorageService;
        this.authService = authService;
    }

    @Transactional
    public MeResponse updateProfile(Long userId, UpdateProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        if (!user.getUsername().equals(request.getUsername())) {
             if (userRepository.existsByUsername(request.getUsername())) {
                 throw new AppException(ErrorCode.INVALID_REQUEST, "Username is already taken");
             }
             user.setUsername(request.getUsername());
             userRepository.save(user);
        }

        return authService.me(userId);
    }

    @Transactional
    public MeResponse uploadAvatar(Long userId, MultipartFile file) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.UNAUTHENTICATED));

        String s3Key = s3StorageService.upload(file);
        user.setImg(s3Key);
        userRepository.save(user);

        return authService.me(userId);
    }
}
