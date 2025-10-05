package cloud.luigi99.solar.playground.common.infrastructure.messaging;

import cloud.luigi99.solar.playground.common.domain.event.DomainEvent;

import java.util.List;

public interface EventPublisher {
    void publish(DomainEvent event);
    void publish(List<DomainEvent> events);
}
