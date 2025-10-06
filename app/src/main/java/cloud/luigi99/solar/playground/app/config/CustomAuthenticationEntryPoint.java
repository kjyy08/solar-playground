package cloud.luigi99.solar.playground.app.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 인증되지 않은 사용자의 접근 처리
 * - API 요청: 401 Unauthorized 응답
 * - 웹 페이지 요청: /login 페이지로 리다이렉트
 */
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException, ServletException {

        String requestURI = request.getRequestURI();

        // API 요청이면 401 에러 반환
        if (requestURI.startsWith("/api/")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
        // 그 외 요청은 로그인 페이지로 리다이렉트
        else {
            response.sendRedirect("/login");
        }
    }
}
