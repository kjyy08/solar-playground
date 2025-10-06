package cloud.luigi99.solar.playground.app.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * 웹 애플리케이션 전역 Security 설정
 * - URL 권한 설정
 * - CORS, CSRF 설정
 * - 예외 처리
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Order(2)  // AuthSecurityConfig 다음에 실행
public class WebSecurityConfig {

    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain webSecurityFilterChain(HttpSecurity http) throws Exception {
        http
                // 모든 요청에 대해 적용 (AuthSecurityConfig 이후에 실행)
                .securityMatcher("/**")

                // CORS, CSRF 설정
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)

                // OAuth2 로그인 페이지 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                )

                // 예외 처리
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                )

                // URL 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 정적 리소스 및 공개 페이지
                        .requestMatchers(
                                "/",
                                "/login/**",
                                "/oauth2/**",
                                "/error",
                                "/favicon.ico",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

//                        // 뷰 페이지 (인증 없이 접근 가능, 추후 변경 가능)
//                        .requestMatchers(
//                                "/dashboard",
//                                "/chat",
//                                "/files",
//                                "/profile"
//                        ).permitAll()

                        // API 엔드포인트 (인증 필요)
                        .requestMatchers("/api/v1/**").authenticated()

                        // 그 외 모든 요청
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
