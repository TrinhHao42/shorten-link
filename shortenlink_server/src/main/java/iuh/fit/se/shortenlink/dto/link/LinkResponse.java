package iuh.fit.se.shortenlink.dto.link;

import iuh.fit.se.shortenlink.entity.enums.LinkType;
import lombok.Builder;

@Builder
public record LinkResponse(Long id, String originalUrl, String slug, LinkType type, Long clickCount) {
}

