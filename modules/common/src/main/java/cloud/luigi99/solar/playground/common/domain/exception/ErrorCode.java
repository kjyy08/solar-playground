package cloud.luigi99.solar.playground.common.domain.exception;

import java.util.Arrays;

public enum ErrorCode {

    // Common Errors
    COMMON_INVALID_PARAMETER("COMMON_INVALID_PARAMETER", "잘못된 요청 파라미터입니다"),
    COMMON_INTERNAL_SERVER_ERROR("COMMON_INTERNAL_SERVER_ERROR", "서버 내부 오류가 발생했습니다"),
    ENTITY_NOT_FOUND("ENTITY_NOT_FOUND", "요청한 데이터를 찾을 수 없습니다"),

    // Auth Errors
    AUTH_UNAUTHENTICATED("AUTH_UNAUTHENTICATED", "인증이 필요합니다"),
    AUTH_UNAUTHORIZED("AUTH_UNAUTHORIZED", "접근 권한이 없습니다"),
    AUTH_INVALID_TOKEN("AUTH_INVALID_TOKEN", "유효하지 않은 토큰입니다"),
    AUTH_TOKEN_EXPIRED("AUTH_TOKEN_EXPIRED", "토큰이 만료되었습니다"),

    // User Errors
    USER_NOT_FOUND("USER_NOT_FOUND", "사용자를 찾을 수 없습니다"),
    USER_EMAIL_DUPLICATE("USER_EMAIL_DUPLICATE", "이미 사용 중인 이메일입니다"),

    // File Errors
    FILE_NOT_FOUND("FILE_NOT_FOUND", "파일을 찾을 수 없습니다"),
    FILE_UPLOAD_FAILED("FILE_UPLOAD_FAILED", "파일 업로드에 실패했습니다"),
    FILE_UNSUPPORTED_FORMAT("FILE_UNSUPPORTED_FORMAT", "지원하지 않는 파일 형식입니다");

    private final String code;
    private final String description;

    ErrorCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return this.code;
    }

    public static ErrorCode fromCode(String code) {
        return Arrays.stream(ErrorCode.values())
                .filter(v -> v.getCode().equals(code))
                .findAny()
                .orElse(null);
    }
}
