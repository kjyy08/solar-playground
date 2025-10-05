package cloud.luigi99.solar.playground.auth.infrastructure.persistence;

import cloud.luigi99.solar.playground.auth.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Refresh Token Repository
 */
@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    /**
     * 이메일로 Refresh Token 조회
     */
    Optional<RefreshToken> findByEmail(String email);

    /**
     * 토큰으로 Refresh Token 조회
     */
    Optional<RefreshToken> findByToken(String token);

    /**
     * 이메일로 Refresh Token 삭제
     */
    void deleteByEmail(String email);

    /**
     * 만료된 토큰 삭제
     */
    void deleteByExpiresAtBefore(LocalDateTime dateTime);
}
