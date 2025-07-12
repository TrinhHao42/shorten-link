package iuh.fit.se.shortenlink.dto.file;

import lombok.Builder;

@Builder
public record FileUploadResponse(String slug, String downloadUrl) {
}

