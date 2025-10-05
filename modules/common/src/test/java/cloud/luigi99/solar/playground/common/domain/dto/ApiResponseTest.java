package cloud.luigi99.solar.playground.common.domain.dto;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Nested
    class 성공_응답_생성 {

        @Nested
        class 데이터가_있는_경우 {

            @Test
            void 데이터와_성공_상태를_포함하는_ApiResponse를_반환한다() {
                // given
                String data = "test data";

                // when
                ApiResponse<String> response = ApiResponse.success(data);

                // then
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getData()).isEqualTo(data);
                assertThat(response.getError()).isNull();
            }
        }

        @Nested
        class 데이터가_없는_경우 {

            @Test
            void 데이터는_null이고_성공_상태인_ApiResponse를_반환한다() {
                // given & when
                ApiResponse<?> response = ApiResponse.success();

                // then
                assertThat(response.isSuccess()).isTrue();
                assertThat(response.getData()).isNull();
                assertThat(response.getError()).isNull();
            }
        }
    }

    @Nested
    class 실패_응답_생성 {

        @Test
        void 에러_메시지와_실패_상태를_포함하는_ApiResponse를_반환한다() {
            // given
            String errorMessage = "test error";

            // when
            ApiResponse<?> response = ApiResponse.error(errorMessage);

            // then
            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getData()).isNull();
            assertThat(response.getError()).isEqualTo(errorMessage);
        }
    }
}
