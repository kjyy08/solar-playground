package cloud.luigi99.solar.playground.user.application;

import cloud.luigi99.solar.playground.user.domain.dto.UserDto;
import cloud.luigi99.solar.playground.user.domain.entity.User;

import java.util.Optional;

/**
 * 사용자 관리 UseCase 인터페이스
 */
public interface UserUseCase {

    /**
     * 사용자 생성 또는 업데이트
     */
    User createOrUpdateUser(
            String email,
            String name,
            String profileImageUrl,
            String provider,
            String providerId
    );

    /**
     * 이메일로 사용자 조회
     */
    Optional<UserDto> findByEmail(String email);

    /**
     * 사용자 ID로 조회
     */
    Optional<UserDto> findById(Long userId);
}
