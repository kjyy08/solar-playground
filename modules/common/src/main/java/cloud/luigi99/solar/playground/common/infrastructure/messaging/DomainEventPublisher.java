package cloud.luigi99.solar.playground.common.infrastructure.messaging;

import cloud.luigi99.solar.playground.common.domain.event.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DomainEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(List<DomainEvent> events) {
        events.forEach(applicationEventPublisher::publishEvent);
    }
}
