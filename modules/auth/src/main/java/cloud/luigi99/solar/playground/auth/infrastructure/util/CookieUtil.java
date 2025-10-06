package cloud.luigi99.solar.playground.auth.infrastructure.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * HTTP 쿠키 관리 유틸리티
 */
@Component
public class CookieUtil {

    @Value("${auth.cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${auth.cookie.domain:}")
    private String cookieDomain;

    /**
     * 쿠키 생성
     *
     * @param name     쿠키 이름
     * @param value    쿠키 값
     * @param maxAge   만료 시간 (초)
     * @return Cookie 객체
     */
    public Cookie createCookie(String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true); // JavaScript 접근 불가 (XSS 방지)
        cookie.setSecure(cookieSecure); // HTTPS only (production)
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);

        if (!cookieDomain.isEmpty()) {
            cookie.setDomain(cookieDomain);
        }

        // SameSite 설정 (CSRF 방지)
        cookie.setAttribute("SameSite", "Lax");

        return cookie;
    }

    /**
     * Access Token 쿠키 생성 (1시간)
     */
    public Cookie createAccessTokenCookie(String token) {
        return createCookie("accessToken", token, 60 * 60);
    }

    /**
     * Refresh Token 쿠키 생성 (7일)
     */
    public Cookie createRefreshTokenCookie(String token) {
        return createCookie("refreshToken", token, 7 * 24 * 60 * 60);
    }

    /**
     * 요청에서 특정 쿠키 값 추출
     *
     * @param request    HTTP 요청
     * @param cookieName 쿠키 이름
     * @return 쿠키 값 (Optional)
     */
    public Optional<String> getCookieValue(HttpServletRequest request, String cookieName) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return Optional.empty();
        }

        return Arrays.stream(cookies)
                .filter(cookie -> cookieName.equals(cookie.getName()))
                .findFirst()
                .map(Cookie::getValue);
    }

    /**
     * 쿠키 삭제 (maxAge를 0으로 설정)
     *
     * @param response   HTTP 응답
     * @param cookieName 삭제할 쿠키 이름
     */
    public void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setMaxAge(0);
        cookie.setHttpOnly(true);

        if (!cookieDomain.isEmpty()) {
            cookie.setDomain(cookieDomain);
        }

        response.addCookie(cookie);
    }

    /**
     * Access Token 쿠키 삭제
     */
    public void deleteAccessTokenCookie(HttpServletResponse response) {
        deleteCookie(response, "accessToken");
    }

    /**
     * Refresh Token 쿠키 삭제
     */
    public void deleteRefreshTokenCookie(HttpServletResponse response) {
        deleteCookie(response, "refreshToken");
    }
}
