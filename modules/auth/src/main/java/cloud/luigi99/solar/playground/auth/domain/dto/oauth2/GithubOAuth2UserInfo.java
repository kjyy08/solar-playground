package cloud.luigi99.solar.playground.auth.domain.dto.oauth2;

import java.util.Map;

/**
 * GitHub OAuth2 사용자 정보 구현체
 */
public record GithubOAuth2UserInfo(Map<String, Object> attributes) implements OAuth2UserInfo {

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getProviderId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getProfileImageUrl() {
        return (String) attributes.get("avatar_url");
    }
}
