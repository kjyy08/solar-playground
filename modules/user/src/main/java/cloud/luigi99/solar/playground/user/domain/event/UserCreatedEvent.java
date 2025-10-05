package cloud.luigi99.solar.playground.user.domain.event;

import cloud.luigi99.solar.playground.common.domain.event.DomainEvent;
import lombok.Builder;

import java.time.Instant;
import java.util.UUID;

/**
 * 새로운 사용자가 생성되었을 때 발행되는 이벤트
 */
@Builder
public record UserCreatedEvent(
        UUID eventId,
        String entityId,
        Instant occurredOn,
        Long userId,
        String email,
        String name
) implements DomainEvent {

    public UserCreatedEvent {
        if (eventId == null) {
            eventId = UUID.randomUUID();
        }
        if (occurredOn == null) {
            occurredOn = Instant.now();
        }
    }

    public static UserCreatedEvent of(Long userId, String email, String name) {
        return UserCreatedEvent.builder()
                .eventId(UUID.randomUUID())
                .entityId(String.valueOf(userId))
                .occurredOn(Instant.now())
                .userId(userId)
                .email(email)
                .name(name)
                .build();
    }
}
