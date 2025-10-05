package cloud.luigi99.solar.playground.user.application;

import cloud.luigi99.solar.playground.user.domain.dto.UserDto;
import cloud.luigi99.solar.playground.user.domain.entity.User;
import cloud.luigi99.solar.playground.user.domain.event.UserCreatedEvent;
import cloud.luigi99.solar.playground.user.domain.event.UserUpdatedEvent;
import cloud.luigi99.solar.playground.user.infrastructure.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 사용자 관리 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * 사용자 생성 또는 업데이트
     * OAuth2 인증 후 호출됩니다.
     */
    @Transactional
    public User createOrUpdateUser(
            String email,
            String name,
            String profileImageUrl,
            String provider,
            String providerId
    ) {
        Optional<User> existingUser = userRepository.findByEmail(email);

        return existingUser.map(user -> updateUser(user, name, profileImageUrl))
                .orElseGet(() -> createUser(email, name, profileImageUrl, provider, providerId));
    }

    /**
     * 이메일로 사용자 조회
     */
    public Optional<UserDto> findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(UserDto::from);
    }

    /**
     * 사용자 ID로 조회
     */
    public Optional<UserDto> findById(Long userId) {
        return userRepository.findById(userId)
                .map(UserDto::from);
    }

    private User createUser(
            String email,
            String name,
            String profileImageUrl,
            String provider,
            String providerId
    ) {
        User user = User.builder()
                .email(email)
                .name(name)
                .profileImageUrl(profileImageUrl)
                .provider(provider)
                .providerId(providerId)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user created: {}", savedUser.getEmail());

        // 이벤트 발행
        eventPublisher.publishEvent(
                UserCreatedEvent.of(savedUser.getId(), savedUser.getEmail(), savedUser.getName())
        );

        return savedUser;
    }

    private User updateUser(User user, String name, String profileImageUrl) {
        user.updateInfo(name, profileImageUrl);
        User updatedUser = userRepository.save(user);
        log.info("User updated: {}", updatedUser.getEmail());

        // 이벤트 발행
        eventPublisher.publishEvent(
                UserUpdatedEvent.of(updatedUser.getId(), updatedUser.getEmail(), updatedUser.getName())
        );

        return updatedUser;
    }
}
