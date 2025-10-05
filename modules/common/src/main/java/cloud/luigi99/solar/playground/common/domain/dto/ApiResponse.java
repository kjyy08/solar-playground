package cloud.luigi99.solar.playground.common.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 * 공통 API 성공 응답 래퍼
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        int status,
        String message,
        T data,
        LocalDateTime timestamp
) {
    public ApiResponse(HttpStatus httpStatus, T data) {
        this(httpStatus.value(), httpStatus.getReasonPhrase(), data, LocalDateTime.now());
    }
}
