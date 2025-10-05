package cloud.luigi99.solar.playground.user.domain.dto;

import cloud.luigi99.solar.playground.user.domain.entity.User;
import lombok.Builder;

/**
 * 사용자 정보 DTO
 */
@Builder
public record UserDto(
        Long id,
        String email,
        String name,
        String profileImageUrl,
        String provider,
        String role
) {
    public static UserDto from(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .name(user.getName())
                .profileImageUrl(user.getProfileImageUrl())
                .provider(user.getProvider())
                .role(user.getRole().name())
                .build();
    }
}
