package iuh.fit.se.shortenlink.controller;

import iuh.fit.se.shortenlink.dto.auth.MeResponse;
import iuh.fit.se.shortenlink.dto.common.ApiResponse;
import iuh.fit.se.shortenlink.dto.user.UpdateProfileRequest;
import iuh.fit.se.shortenlink.service.CurrentUserService;
import iuh.fit.se.shortenlink.service.UserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final CurrentUserService currentUserService;

    public UserController(UserService userService, CurrentUserService currentUserService) {
        this.userService = userService;
        this.currentUserService = currentUserService;
    }

    @PutMapping("/me/profile")
    public ApiResponse<MeResponse> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        Long userId = currentUserService.currentUserId();
        return ApiResponse.ok(userService.updateProfile(userId, request));
    }

    @PostMapping("/me/avatar")
    public ApiResponse<MeResponse> uploadAvatar(@RequestParam("file") MultipartFile file) {
        Long userId = currentUserService.currentUserId();
        return ApiResponse.ok(userService.uploadAvatar(userId, file));
    }
}
