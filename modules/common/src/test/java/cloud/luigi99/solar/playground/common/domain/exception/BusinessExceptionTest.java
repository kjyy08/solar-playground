package cloud.luigi99.solar.playground.common.domain.exception;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BusinessExceptionTest {

    @Nested
    class 생성자_테스트 {

        @Nested
        class ErrorCode만_받는_경우 {

            @Test
            void ErrorCode의_기본_메시지를_사용하여_예외를_생성한다() {
                // given
                ErrorCode errorCode = ErrorCode.USER_NOT_FOUND;

                // when
                BusinessException exception = new BusinessException(errorCode);

                // then
                assertThat(exception.getErrorCode()).isEqualTo(errorCode);
                assertThat(exception.getMessage()).isEqualTo(errorCode.getDescription());
            }
        }

        @Nested
        class ErrorCode와_커스텀_메시지를_받는_경우 {

            @Test
            void 커스텀_메시지를_사용하여_예외를_생성한다() {
                // given
                ErrorCode errorCode = ErrorCode.COMMON_INVALID_PARAMETER;
                String customMessage = "The user ID cannot be null.";

                // when
                BusinessException exception = new BusinessException(customMessage, errorCode);

                // then
                assertThat(exception.getErrorCode()).isEqualTo(errorCode);
                assertThat(exception.getMessage()).isEqualTo(customMessage);
            }
        }
    }
}
