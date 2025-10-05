package cloud.luigi99.solar.playground.auth.application;

import cloud.luigi99.solar.playground.auth.domain.dto.TokenResponse;

/**
 * 인증 관련 UseCase 인터페이스
 */
public interface AuthUseCase {

    /**
     * JWT 토큰 생성 및 Refresh Token 저장
     */
    TokenResponse createTokens(String email);

    /**
     * Refresh Token을 사용하여 새로운 Access Token과 Refresh Token 발급
     */
    TokenResponse refreshToken(String refreshToken);

    /**
     * 로그아웃 - Refresh Token 삭제
     */
    void logout(String email);
}
