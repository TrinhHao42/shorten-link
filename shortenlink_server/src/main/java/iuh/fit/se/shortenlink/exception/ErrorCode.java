package iuh.fit.se.shortenlink.exception;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    UNAUTHENTICATED(401, "Unauthenticated"),
    FORBIDDEN(403, "Forbidden"),
    USER_ALREADY_EXISTS(409, "User already exists"),
    INVALID_CREDENTIALS(401, "Invalid credentials"),
    LINK_NOT_FOUND(404, "Link not found"),
    FILE_NOT_FOUND(404, "File not found"),
    SLUG_COLLISION(409, "Slug already exists"),
    RATE_LIMIT_EXCEEDED(429, "Rate limit exceeded"),
    FILE_UPLOAD_FAILED(500, "File upload failed"),
    INVALID_REQUEST(400, "Invalid request"),
    INTERNAL_ERROR(500, "Internal error");

    int code;
    String message;
}
