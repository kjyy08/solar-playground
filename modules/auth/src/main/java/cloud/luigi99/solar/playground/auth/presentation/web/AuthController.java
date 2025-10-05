package cloud.luigi99.solar.playground.auth.presentation.web;

import cloud.luigi99.solar.playground.auth.application.AuthUseCase;
import cloud.luigi99.solar.playground.auth.domain.dto.TokenResponse;
import cloud.luigi99.solar.playground.common.domain.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 인증 관련 REST API 컨트롤러
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthUseCase authUseCase;

    /**
     * Refresh Token을 사용하여 새로운 Access Token 발급
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<TokenResponse>> refresh(
            @RequestHeader("Authorization") String refreshToken
    ) {
        String token = refreshToken.replace("Bearer ", "");
        TokenResponse tokenResponse = authUseCase.refreshToken(token);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, tokenResponse));
    }

    /**
     * 로그아웃 (Refresh Token 삭제)
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@AuthenticationPrincipal String email) {
        authUseCase.logout(email);
        return ResponseEntity.ok(new ApiResponse<>(HttpStatus.OK, null));
    }
}
