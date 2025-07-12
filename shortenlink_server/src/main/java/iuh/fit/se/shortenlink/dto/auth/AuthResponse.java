package iuh.fit.se.shortenlink.dto.auth;

import lombok.Builder;

@Builder
public record AuthResponse(String accessToken) {
}

