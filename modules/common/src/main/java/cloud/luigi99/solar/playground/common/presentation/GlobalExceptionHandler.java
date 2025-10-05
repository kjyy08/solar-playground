package cloud.luigi99.solar.playground.common.presentation;

import cloud.luigi99.solar.playground.common.domain.dto.ErrorResponse;
import cloud.luigi99.solar.playground.common.domain.exception.BusinessException;
import cloud.luigi99.solar.playground.common.domain.exception.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 전역 예외 처리 핸들러
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * BusinessException 처리
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException e) {
        log.warn("BusinessException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        ErrorResponse response = ErrorResponse.of(errorCode.getCode(), errorCode.getDescription());
        return ResponseEntity
                .status(getHttpStatus(errorCode))
                .body(response);
    }

    /**
     * Validation 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException e) {
        log.warn("MethodArgumentNotValidException: {}", e.getMessage());

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));

        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_INVALID_PARAMETER.getCode(),
                errorMessage.isEmpty() ? ErrorCode.COMMON_INVALID_PARAMETER.getDescription() : errorMessage
        );

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * 기타 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("Unexpected Exception: ", e);
        ErrorResponse response = ErrorResponse.of(
                ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getCode(),
                ErrorCode.COMMON_INTERNAL_SERVER_ERROR.getDescription()
        );
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    /**
     * ErrorCode에 따른 HTTP 상태 코드 매핑
     */
    private HttpStatus getHttpStatus(ErrorCode errorCode) {
        return switch (errorCode) {
            case AUTH_UNAUTHENTICATED -> HttpStatus.UNAUTHORIZED;
            case AUTH_UNAUTHORIZED -> HttpStatus.FORBIDDEN;
            case AUTH_INVALID_TOKEN, AUTH_TOKEN_EXPIRED -> HttpStatus.UNAUTHORIZED;
            case USER_NOT_FOUND, FILE_NOT_FOUND, ENTITY_NOT_FOUND -> HttpStatus.NOT_FOUND;
            case USER_EMAIL_DUPLICATE -> HttpStatus.CONFLICT;
            case COMMON_INVALID_PARAMETER, FILE_UNSUPPORTED_FORMAT -> HttpStatus.BAD_REQUEST;
            case FILE_UPLOAD_FAILED -> HttpStatus.INTERNAL_SERVER_ERROR;
            default -> HttpStatus.INTERNAL_SERVER_ERROR;
        };
    }
}
