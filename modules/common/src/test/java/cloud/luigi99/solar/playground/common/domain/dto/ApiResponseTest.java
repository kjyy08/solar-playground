package cloud.luigi99.solar.playground.common.domain.dto;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ApiResponseTest {

    @Test
    void 데이터와_HTTP_상태를_포함하는_ApiResponse를_생성한다() {
        // given
        String data = "test data";
        HttpStatus httpStatus = HttpStatus.OK;

        // when
        ApiResponse<String> response = new ApiResponse<>(httpStatus, data);

        // then
        assertThat(response.status()).isEqualTo(200);
        assertThat(response.message()).isEqualTo("OK");
        assertThat(response.data()).isEqualTo(data);
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    void 데이터가_null인_ApiResponse를_생성한다() {
        // given
        HttpStatus httpStatus = HttpStatus.NO_CONTENT;

        // when
        ApiResponse<Void> response = new ApiResponse<>(httpStatus, null);

        // then
        assertThat(response.status()).isEqualTo(204);
        assertThat(response.message()).isEqualTo("No Content");
        assertThat(response.data()).isNull();
        assertThat(response.timestamp()).isNotNull();
    }

    @Test
    void CREATED_상태의_ApiResponse를_생성한다() {
        // given
        String data = "created resource";
        HttpStatus httpStatus = HttpStatus.CREATED;

        // when
        ApiResponse<String> response = new ApiResponse<>(httpStatus, data);

        // then
        assertThat(response.status()).isEqualTo(201);
        assertThat(response.message()).isEqualTo("Created");
        assertThat(response.data()).isEqualTo(data);
    }
}
