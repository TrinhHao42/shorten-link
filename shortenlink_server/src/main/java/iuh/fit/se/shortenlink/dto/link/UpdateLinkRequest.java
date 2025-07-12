package iuh.fit.se.shortenlink.dto.link;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateLinkRequest {
    private String originalUrl;
    private String slug;
}

