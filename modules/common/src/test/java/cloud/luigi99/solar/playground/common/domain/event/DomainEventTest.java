package cloud.luigi99.solar.playground.common.domain.event;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class DomainEventTest {

    private record TestEvent(String entityId) implements DomainEvent {}

    @Test
    void eventId와_eventType과_occurredOn을_정상적으로_제공한다() {
        // given
        String entityId = "test-entity-123";
        Instant beforeCreation = Instant.now();

        // when
        DomainEvent event = new TestEvent(entityId);
        Instant afterCreation = Instant.now();

        // then
        assertThat(event.eventId()).isInstanceOf(UUID.class);
        assertThat(event.eventType()).isEqualTo("테스트_이벤트");
        assertThat(event.entityId()).isEqualTo(entityId);
        assertThat(event.occurredOn()).isBetween(beforeCreation, afterCreation);
    }

}
