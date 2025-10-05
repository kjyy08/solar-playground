package cloud.luigi99.solar.playground.auth.domain.event;

import cloud.luigi99.solar.playground.common.domain.event.DomainEvent;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * OAuth2 인증 성공 시 발행되는 이벤트입니다.
 * 다른 모듈(user)이 이 이벤트를 구독하여 사용자 정보를 생성/업데이트합니다.
 */
@Builder
public record UserAuthenticationRequestedEvent(
        UUID eventId,
        String entityId,
        Instant occurredOn,
        String email,
        String name,
        String profileImageUrl,
        String provider,
        String providerId
) implements DomainEvent {

    public UserAuthenticationRequestedEvent {
        if (eventId == null) {
            eventId = UUID.randomUUID();
        }
        if (occurredOn == null) {
            occurredOn = Instant.now();
        }
    }

    public static UserAuthenticationRequestedEvent of(
            String email,
            String name,
            String profileImageUrl,
            String provider,
            String providerId
    ) {
        return UserAuthenticationRequestedEvent.builder()
                .eventId(UUID.randomUUID())
                .entityId(email)
                .occurredOn(Instant.now())
                .email(email)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .provider(provider)
                .providerId(providerId)
                .build();
    }
}
