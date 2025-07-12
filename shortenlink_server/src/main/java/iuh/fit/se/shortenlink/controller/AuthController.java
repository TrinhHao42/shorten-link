package iuh.fit.se.shortenlink.controller;

import iuh.fit.se.shortenlink.dto.auth.AuthResponse;
import iuh.fit.se.shortenlink.dto.auth.LoginRequest;
import iuh.fit.se.shortenlink.dto.auth.RefreshRequest;
import iuh.fit.se.shortenlink.dto.auth.RegisterRequest;
import iuh.fit.se.shortenlink.dto.auth.MeResponse;
import iuh.fit.se.shortenlink.dto.common.ApiResponse;
import iuh.fit.se.shortenlink.service.AuthService;
import iuh.fit.se.shortenlink.service.CurrentUserService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final CurrentUserService currentUserService;

    public AuthController(AuthService authService, CurrentUserService currentUserService) {
        this.authService = authService;
        this.currentUserService = currentUserService;
    }

    @PostMapping("/register")
    public ApiResponse<Void> register(@Valid @RequestBody RegisterRequest request) {
        authService.register(request);
        return ApiResponse.ok(null);
    }

    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ApiResponse<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ApiResponse.ok(authService.refresh(request));
    }

    @GetMapping("/me")
    public ApiResponse<MeResponse> me() {
        Long userId = currentUserService.currentUserId();
        return ApiResponse.ok(authService.me(userId));
    }
}
