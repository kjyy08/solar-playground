package cloud.luigi99.solar.playground.auth.domain.dto.oauth2;

import java.util.Map;

/**
 * OAuth2 제공자로부터 받은 사용자 정보를 추상화하는 인터페이스
 */
public interface OAuth2UserInfo {

    /**
     * OAuth2 제공자 이름
     */
    String getProvider();

    /**
     * 제공자가 부여한 사용자 고유 ID
     */
    String getProviderId();

    /**
     * 사용자 이메일
     */
    String getEmail();

    /**
     * 사용자 이름
     */
    String getName();

    /**
     * 프로필 이미지 URL
     */
    String getProfileImageUrl();

    /**
     * 원본 attributes
     */
    Map<String, Object> attributes();
}
