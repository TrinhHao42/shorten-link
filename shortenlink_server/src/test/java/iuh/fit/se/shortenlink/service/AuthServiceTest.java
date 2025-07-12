package iuh.fit.se.shortenlink.service;

import iuh.fit.se.shortenlink.dto.auth.RegisterRequest;
import iuh.fit.se.shortenlink.entity.User;
import iuh.fit.se.shortenlink.exception.AppException;
import iuh.fit.se.shortenlink.exception.ErrorCode;
import iuh.fit.se.shortenlink.repository.UserRepository;
import iuh.fit.se.shortenlink.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private RefreshTokenStore refreshTokenStore;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_shouldSaveUserWithoutIssuingTokens() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("Test@Example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret123")).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        authService.register(request);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        User savedUser = userCaptor.getValue();
        assertEquals("test@example.com", savedUser.getEmail());
        assertEquals("test", savedUser.getUsername());
        assertEquals("hashed-password", savedUser.getPasswordHash());
        verify(jwtService, never()).generateAccessToken(any(), any());
        verify(jwtService, never()).generateRefreshToken(any(), any());
        verify(refreshTokenStore, never()).save(any(), any());
    }

    @Test
    void register_shouldThrowWhenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("secret123");

        when(userRepository.existsByEmail("existing@example.com")).thenReturn(true);

        AppException exception = assertThrows(AppException.class, () -> authService.register(request));

        assertEquals(ErrorCode.USER_ALREADY_EXISTS, exception.getErrorCode());
        verify(userRepository, never()).save(any(User.class));
        verify(jwtService, never()).generateAccessToken(any(), any());
        verify(jwtService, never()).generateRefreshToken(any(), any());
        verify(refreshTokenStore, never()).save(any(), any());
    }
}
