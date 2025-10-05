package cloud.luigi99.solar.playground.auth.domain.dto.oauth2;

import java.util.Map;

/**
 * OAuth2 제공자별 UserInfo 객체를 생성하는 팩토리 클래스
 */
public class OAuth2UserInfoFactory {

    /**
     * OAuth2 제공자 이름과 attributes를 받아 적절한 OAuth2UserInfo 구현체를 반환합니다.
     *
     * @param registrationId OAuth2 제공자 이름 (github, google 등)
     * @param attributes OAuth2 제공자로부터 받은 사용자 정보
     * @return OAuth2UserInfo 구현체
     * @throws IllegalArgumentException 지원하지 않는 OAuth2 제공자인 경우
     */
    public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
        return switch (registrationId.toLowerCase()) {
            case "github" -> new GithubOAuth2UserInfo(attributes);
            case "google" -> new GoogleOAuth2UserInfo(attributes);
            // 추가 OAuth2 제공자는 여기에 case를 추가하면 됩니다
            // case "kakao" -> new KakaoOAuth2UserInfo(attributes);
            // case "naver" -> new NaverOAuth2UserInfo(attributes);
            default -> throw new IllegalArgumentException(
                    "Unsupported OAuth2 provider: " + registrationId
            );
        };
    }
}
