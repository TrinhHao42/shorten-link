package iuh.fit.se.shortenlink.dto.notification;

import lombok.Builder;

@Builder
public record ShareLinkMessage(String type, Long senderId, String receiverEmail, String link, String message) {
}

