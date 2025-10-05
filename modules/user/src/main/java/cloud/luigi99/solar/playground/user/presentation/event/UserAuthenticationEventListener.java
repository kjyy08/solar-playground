package cloud.luigi99.solar.playground.user.presentation.event;

import cloud.luigi99.solar.playground.auth.domain.event.UserAuthenticationRequestedEvent;
import cloud.luigi99.solar.playground.user.application.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * auth 모듈에서 발행한 UserAuthenticationRequestedEvent를 구독하는 리스너
 * OAuth2 인증이 성공하면 사용자 정보를 저장/업데이트합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserAuthenticationEventListener {

    private final UserService userService;

    @EventListener
    @Transactional
    public void onUserAuthenticated(UserAuthenticationRequestedEvent event) {
        log.info("Received UserAuthenticationRequestedEvent for email: {}", event.email());

        userService.createOrUpdateUser(
                event.email(),
                event.name(),
                event.profileImageUrl(),
                event.provider(),
                event.providerId()
        );

        log.info("User information saved/updated for: {}", event.email());
    }
}
