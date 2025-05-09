package an.inhaintegration.oauth2;

import lombok.AllArgsConstructor;

import java.util.Map;

@AllArgsConstructor
public class GithubUserDetails implements OAuth2UserInfo {

    private Map<String, Object> attributes;

    @Override
    public String getProvider() {
        return "github";
    }

    @Override
    public String getProviderId() {
        return attributes.get("id").toString();
    }

    @Override
    public String getEmail() {
        return null;
    }

    @Override
    public String getName() {
        // name 없으면 login 사용
        return (attributes.get("name") != null) ? attributes.get("name").toString() : attributes.get("login").toString();
    }

    @Override
    public String getPicture() {
        return (String) (attributes.get("avatar_url"));
    }

}