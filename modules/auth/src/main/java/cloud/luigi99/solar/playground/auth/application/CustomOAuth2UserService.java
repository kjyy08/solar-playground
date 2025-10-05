package cloud.luigi99.solar.playground.auth.application;

import cloud.luigi99.solar.playground.auth.domain.dto.oauth2.OAuth2UserInfo;
import cloud.luigi99.solar.playground.auth.domain.dto.oauth2.OAuth2UserInfoFactory;
import cloud.luigi99.solar.playground.auth.domain.event.UserAuthenticationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

/**
 * OAuth2 인증 후 사용자 정보를 처리하는 서비스
 * 팩토리 패턴을 사용하여 다양한 OAuth2 제공자를 지원합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final ApplicationEventPublisher eventPublisher;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        try {
            return processOAuth2User(userRequest, oAuth2User);
        } catch (Exception e) {
            log.error("Error processing OAuth2 user", e);
            throw new OAuth2AuthenticationException(e.getMessage());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest userRequest, OAuth2User oAuth2User) {
        String registrationId = userRequest.getClientRegistration().getRegistrationId();

        // 팩토리를 사용하여 제공자별 UserInfo 생성
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(
                registrationId,
                oAuth2User.getAttributes()
        );

        // 이메일 필수 검증
        if (userInfo.getEmail() == null || userInfo.getEmail().isEmpty()) {
            throw new OAuth2AuthenticationException("Email not found from OAuth2 provider");
        }

        // 이벤트 발행: user 모듈이 이 이벤트를 구독하여 사용자 정보를 저장/업데이트
        publishUserAuthenticationEvent(userInfo);

        return oAuth2User;
    }

    private void publishUserAuthenticationEvent(OAuth2UserInfo userInfo) {
        UserAuthenticationRequestedEvent event = UserAuthenticationRequestedEvent.of(
                userInfo.getEmail(),
                userInfo.getName(),
                userInfo.getProfileImageUrl(),
                userInfo.getProvider(),
                userInfo.getProviderId()
        );

        log.info("Publishing UserAuthenticationRequestedEvent for provider: {}, email: {}",
                userInfo.getProvider(), userInfo.getEmail());
        eventPublisher.publishEvent(event);
    }
}

