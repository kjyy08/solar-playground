package cloud.luigi99.solar.playground.auth.infrastructure.security;

import cloud.luigi99.solar.playground.auth.application.AuthService;
import cloud.luigi99.solar.playground.auth.domain.dto.TokenResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 생성하여 클라이언트에 전달하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthService authService;

    @Value("${auth.oauth2.redirect-uri:http://localhost:8080/login/success}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        if (email == null) {
            log.error("Email not found in OAuth2User attributes");
            getRedirectStrategy().sendRedirect(request, response, "/login?error");
            return;
        }

        // JWT 토큰 생성 및 Refresh Token 저장
        TokenResponse tokenResponse = authService.createTokens(email);

        log.info("JWT tokens generated for user: {}", email);

        // 토큰을 쿼리 파라미터로 전달 (프론트엔드에서 처리)
        String targetUrl = UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("accessToken", tokenResponse.accessToken())
                .queryParam("refreshToken", tokenResponse.refreshToken())
                .build()
                .toUriString();

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}
