package cloud.luigi99.solar.playground.common.domain.model;

import cloud.luigi99.solar.playground.common.infrastructure.config.JpaConfig;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(JpaConfig.class)
class BaseEntityTest {

    @Autowired
    private TestEntityRepository testEntityRepository;

    @Nested
    class 엔티티_저장_시 {

        @Test
        void createdAt과_updatedAt이_자동으로_생성된다() {
            // given
            var newEntity = new TestEntity("test-name");

            // when
            TestEntity savedEntity = testEntityRepository.save(newEntity);

            // then
            assertThat(savedEntity.getCreatedAt()).isNotNull();
            assertThat(savedEntity.getUpdatedAt()).isNotNull();
            assertThat(savedEntity.getCreatedAt()).isEqualTo(savedEntity.getUpdatedAt());
        }
    }

    @Nested
    class 엔티티_수정_시 {

        @Test
        void updatedAt이_자동으로_갱신된다() throws InterruptedException {
            // given
            var newEntity = new TestEntity("initial-name");
            TestEntity savedEntity = testEntityRepository.save(newEntity);
            Thread.sleep(10); // 시간차를 두기 위해 잠시 대기

            // when
            savedEntity.setName("updated-name");
            TestEntity updatedEntity = testEntityRepository.save(savedEntity);

            // then
            assertThat(updatedEntity.getCreatedAt()).isEqualTo(savedEntity.getCreatedAt());
            assertThat(updatedEntity.getUpdatedAt()).isAfter(savedEntity.getCreatedAt());
        }
    }
}
