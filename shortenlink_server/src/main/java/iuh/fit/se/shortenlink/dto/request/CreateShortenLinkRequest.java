package iuh.fit.se.shortenlink.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class CreateShortenLinkRequest {
    String shortenLink;
    @NotBlank(message = "Link không được để trống")
    String resourceLink;
}
