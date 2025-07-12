package iuh.fit.se.shortenlink.dto.auth;

import lombok.Builder;

import java.time.Instant;

@Builder
public record MeResponse(Long id, String email, String username, String img, Instant createdAt) {
}

