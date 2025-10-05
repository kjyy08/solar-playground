package cloud.luigi99.solar.playground.user.presentation.web;

import cloud.luigi99.solar.playground.common.domain.dto.ApiResponse;
import cloud.luigi99.solar.playground.common.domain.exception.BusinessException;
import cloud.luigi99.solar.playground.common.domain.exception.ErrorCode;
import cloud.luigi99.solar.playground.user.application.UserUseCase;
import cloud.luigi99.solar.playground.user.domain.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 사용자 정보 조회 API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserUseCase userUseCase;

    /**
     * 현재 로그인한 사용자 정보 조회
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUser(
            @AuthenticationPrincipal String email
    ) {
        UserDto user = userUseCase.findByEmail(email)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, user));
    }

    /**
     * 사용자 ID로 조회
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long userId) {
        UserDto user = userUseCase.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, user));
    }
}
