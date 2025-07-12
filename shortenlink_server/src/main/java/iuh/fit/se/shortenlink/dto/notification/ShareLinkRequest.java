package iuh.fit.se.shortenlink.dto.notification;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ShareLinkRequest {
    @Email
    @NotBlank
    private String receiverEmail;

    @NotBlank
    private String shortLink;

    private String message;
}

