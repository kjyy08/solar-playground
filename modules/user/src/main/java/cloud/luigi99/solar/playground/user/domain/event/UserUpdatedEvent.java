package cloud.luigi99.solar.playground.user.domain.event;

import cloud.luigi99.solar.playground.common.domain.event.DomainEvent;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * 사용자 정보가 업데이트되었을 때 발행되는 이벤트
 */
@Builder
public record UserUpdatedEvent(
        UUID eventId,
        String entityId,
        Instant occurredOn,
        Long userId,
        String email,
        String name
) implements DomainEvent {

    public UserUpdatedEvent {
        if (eventId == null) {
            eventId = UUID.randomUUID();
        }
        if (occurredOn == null) {
            occurredOn = Instant.now();
        }
    }

    public static UserUpdatedEvent of(Long userId, String email, String name) {
        return UserUpdatedEvent.builder()
                .eventId(UUID.randomUUID())
                .entityId(String.valueOf(userId))
                .occurredOn(Instant.now())
                .userId(userId)
                .email(email)
                .name(name)
                .build();
    }
}
