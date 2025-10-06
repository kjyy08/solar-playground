package cloud.luigi99.solar.playground.auth.config;

import cloud.luigi99.solar.playground.auth.application.CustomOAuth2UserService;
import cloud.luigi99.solar.playground.auth.application.JwtTokenProvider;
import cloud.luigi99.solar.playground.auth.infrastructure.security.JwtAuthenticationFilter;
import cloud.luigi99.solar.playground.auth.infrastructure.security.OAuth2AuthenticationFailureHandler;
import cloud.luigi99.solar.playground.auth.infrastructure.security.OAuth2AuthenticationSuccessHandler;
import cloud.luigi99.solar.playground.auth.infrastructure.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 인증(Authentication) 관련 Security 설정
 * - JWT 인증 필터
 * - OAuth2 로그인 설정
 */
@Configuration
@RequiredArgsConstructor
@Order(1)  // 먼저 실행
public class AuthSecurityConfig {

    private final JwtTokenProvider jwtTokenProvider;
    private final CookieUtil cookieUtil;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2SuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2FailureHandler;

    @Bean
    public SecurityFilterChain authSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // OAuth2 및 API 요청에만 적용
                .securityMatcher("/oauth2/**", "/api/**")

                // 기본 인증 비활성화
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)

                // 세션 정책: STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2SuccessHandler)
                        .failureHandler(oAuth2FailureHandler)
                )

                // JWT 인증 필터 추가
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtTokenProvider, cookieUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
}
