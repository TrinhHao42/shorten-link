package iuh.fit.se.shortenlink.dto.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
public record ApiResponse<T>(int code, String message, T result) {
    public static <T> ApiResponse<T> ok(T result) {
        return ApiResponse.<T>builder().code(202).message("success").result(result).build();
    }
}
