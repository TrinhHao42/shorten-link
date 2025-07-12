package iuh.fit.se.shortenlink.dto.link;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateLinkRequest {
    @NotBlank
    private String originalUrl;
    private String customSlug;
    private String password;
}

