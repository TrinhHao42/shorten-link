package iuh.fit.se.shortenlink.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class CreateShortenLinkResponse {
    String shortenLink;
}
