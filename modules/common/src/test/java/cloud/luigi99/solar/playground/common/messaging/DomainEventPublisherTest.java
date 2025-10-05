package cloud.luigi99.solar.playground.common.messaging;

import cloud.luigi99.solar.playground.common.domain.event.DomainEvent;
import cloud.luigi99.solar.playground.common.infrastructure.messaging.DomainEventPublisher;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = {DomainEventPublisher.class})
class DomainEventPublisherTest {

    @Autowired
    private DomainEventPublisher domainEventPublisher;

    @MockitoBean
    private ApplicationEventPublisher applicationEventPublisher;

    private record TestEvent(String entityId) implements DomainEvent {}

    @Nested
    class publish_메소드는 {

        @Nested
        class 단일_이벤트가_주어지면 {

            @Test
            void 내부_퍼블리셔를_한_번_호출한다() {
                // given
                var event = new TestEvent("test-id");

                // when
                domainEventPublisher.publish(event);

                // then
                verify(applicationEventPublisher, times(1)).publishEvent(event);
            }
        }

        @Nested
        class 이벤트_리스트가_주어지면 {

            @Test
            void 리스트의_각_이벤트에_대해_내부_퍼블리셔를_호출한다() {
                // given
                var event1 = new TestEvent("id-1");
                var event2 = new TestEvent("id-2");
                List<DomainEvent> events = List.of(event1, event2);

                // when
                domainEventPublisher.publish(events);

                // then
                verify(applicationEventPublisher, times(1)).publishEvent(event1);
                verify(applicationEventPublisher, times(1)).publishEvent(event2);
            }
        }

        @Nested
        class null_이벤트가_주어지면 {

            @Test
            void 예외가_발생해야_한다() {
                // given
                DomainEvent event = null;

                // when & then
                assertThrows(NullPointerException.class, () -> domainEventPublisher.publish(event));
            }
        }

        @Nested
        class null_리스트가_주어지면 {

            @Test
            void 예외가_발생해야_한다() {
                // given
                List<DomainEvent> events = null;

                // when & then
                assertThrows(NullPointerException.class, () -> domainEventPublisher.publish(events));
            }
        }
    }
}
