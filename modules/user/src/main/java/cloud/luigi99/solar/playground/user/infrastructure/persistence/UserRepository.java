package cloud.luigi99.solar.playground.user.infrastructure.persistence;

import cloud.luigi99.solar.playground.user.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 사용자 Repository
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * 이메일로 사용자 조회
     */
    Optional<User> findByEmail(String email);

    /**
     * Provider와 ProviderId로 사용자 조회
     */
    Optional<User> findByProviderAndProviderId(String provider, String providerId);

    /**
     * 이메일 존재 여부 확인
     */
    boolean existsByEmail(String email);
}
