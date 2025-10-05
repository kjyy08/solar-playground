package cloud.luigi99.solar.playground.common.domain.event;

import java.time.Instant;
import java.util.UUID;

/**
 * 모든 도메인 이벤트가 구현해야 하는 공통 인터페이스입니다.
 * 도메인 이벤트는 과거에 발생한 '사실'을 나타내며, 불변(immutable)이어야 합니다.
 */
public interface DomainEvent {

    /**
     * 이벤트의 고유 식별자를 반환합니다.
     *
     * @return UUID
     */
    default UUID eventId() {
        return UUID.randomUUID();
    }

    /**
     * 이벤트의 타입을 문자열로 반환합니다.
     * default 메소드를 사용하여 구현 클래스의 이름을 기본값으로 사용합니다.
     *
     * @return String 이벤트 타입 (예: "FileUploadedEvent")
     */
    default String eventType() {
        return this.getClass().getSimpleName();
    }

    /**
     * 이벤트의 주체가 되는 엔티티의 식별자를 반환합니다.
     *
     * @return String 형태의 엔티티 ID
     */
    String entityId();

    /**
     * 이벤트가 발생한 시간을 반환합니다.
     *
     * @return Instant
     */
    default Instant occurredOn() {
        return Instant.now();
    }
}
