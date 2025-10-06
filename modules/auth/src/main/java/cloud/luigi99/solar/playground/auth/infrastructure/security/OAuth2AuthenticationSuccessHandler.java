package cloud.luigi99.solar.playground.auth.infrastructure.security;

import cloud.luigi99.solar.playground.auth.application.AuthUseCase;
import cloud.luigi99.solar.playground.auth.domain.dto.TokenResponse;
import cloud.luigi99.solar.playground.auth.infrastructure.util.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * OAuth2 로그인 성공 시 JWT 토큰을 생성하여 클라이언트에 전달하는 핸들러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final AuthUseCase authUseCase;
    private final CookieUtil cookieUtil;

    @Value("${auth.oauth2.redirect-uri:/dashboard}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        log.info("OAuth2 authentication success handler called");

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");

        log.info("OAuth2User attributes: {}", oAuth2User.getAttributes());
        log.info("Email from OAuth2User: {}", email);

        if (email == null) {
            log.error("Email not found in OAuth2User attributes");
            getRedirectStrategy().sendRedirect(request, response, "/login?error=no_email");
            return;
        }

        try {
            // JWT 토큰 생성 및 Refresh Token 저장
            TokenResponse tokenResponse = authUseCase.createTokens(email);

            log.info("JWT tokens generated for user: {}", email);

            // 쿠키에 토큰 저장
            response.addCookie(cookieUtil.createAccessTokenCookie(tokenResponse.accessToken()));
            response.addCookie(cookieUtil.createRefreshTokenCookie(tokenResponse.refreshToken()));

            log.info("Cookies added to response. Redirecting to: {}", redirectUri);

            // 대시보드로 리다이렉트
            getRedirectStrategy().sendRedirect(request, response, redirectUri);

            log.info("Redirect completed successfully");

        } catch (Exception e) {
            log.error("Error during OAuth2 authentication success handling", e);
            getRedirectStrategy().sendRedirect(request, response, "/login?error=token_creation_failed");
        }
    }
}
