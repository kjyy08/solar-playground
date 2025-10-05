package cloud.luigi99.solar.playground.common.domain.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorCodeTest {

    @Nested
    class 유효한_코드가_주어지면 {

        @Test
        void 해당하는_ErrorCode_객체를_반환한다() {
            // given
            String code = "USER_NOT_FOUND";

            // when
            ErrorCode foundCode = ErrorCode.fromCode(code);

            // then
            assertThat(foundCode).isNotNull();
            assertThat(foundCode).isEqualTo(ErrorCode.USER_NOT_FOUND);
            assertThat(foundCode.getCode()).isEqualTo("USER_NOT_FOUND");
            assertThat(foundCode.getDescription()).isEqualTo("사용자를 찾을 수 없습니다");
        }
    }

    @Nested
    class 존재하지_않는_코드가_주어지면 {

        @Test
        void null을_반환한다() {
            // given
            String code = "NON_EXISTENT_CODE";

            // when
            ErrorCode foundCode = ErrorCode.fromCode(code);

            // then
            assertThat(foundCode).isNull();
        }
    }

    @Test
    void ErrorCode의_코드_문자열을_반환한다() {
        // given
        ErrorCode errorCode = ErrorCode.AUTH_UNAUTHORIZED;

        // when
        String codeString = errorCode.toString();

        // then
        assertThat(codeString).isEqualTo("AUTH_UNAUTHORIZED");
    }

}
