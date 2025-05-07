package an.inhaintegration.service;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.*;
import an.inhaintegration.repository.StudentRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class CustomOauth2UserService extends DefaultOAuth2UserService {

    private final StudentRepository studentRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2UserInfo oAuth2UserInfo = switch (provider) {
            case "google" -> new GoogleUserDetails(oAuth2User.getAttributes());
            case "kakao" -> new KakaoUserDetails(oAuth2User.getAttributes());
            case "naver" -> new NaverUserDetails(oAuth2User.getAttributes());
            case "github" -> new GithubUserDetails(oAuth2User.getAttributes());
            default -> null;
        };

        String providerId = oAuth2UserInfo.getProviderId();
        String email = (!provider.equals("github")) ? oAuth2UserInfo.getEmail() : fetchEmailFromGitHub(userRequest.getAccessToken().getTokenValue());
        String name = oAuth2UserInfo.getName();
        String picture = oAuth2UserInfo.getPicture();

        Student student = studentRepository.findByLoginIdAndProvider(email, provider).orElseGet(() -> {
            Student newStudent = Student.builder()
                    .loginId(email)
                    .name(name)
                    .provider(provider)
                    .providerId(providerId)
                    .picture(picture)
                    .build();
            return studentRepository.save(newStudent);
        });

        return new CustomUserDetails(student, oAuth2User.getAttributes());
    }

    // GitHub /user/emails API를 호출하여 이메일을 가져오는 메서드
    private String fetchEmailFromGitHub(String accessToken) {
        String url = "https://api.github.com/user/emails";

        // API 호출 시 Authorization 헤더에 Bearer Token을 전달해야 함
        String response = new RestTemplate().exchange(
                url,
                HttpMethod.GET,
                new HttpEntity<>(createHeaders(accessToken)),
                String.class
        ).getBody();

        // JSON 응답 파싱
        JsonArray emails = JsonParser.parseString(response).getAsJsonArray();

        // 이메일 배열을 순회하면서 필요한 이메일 찾기
        for (JsonElement email : emails) {
            JsonObject emailObject = email.getAsJsonObject();
            if (emailObject.get("primary").getAsBoolean()) {
                return emailObject.get("email").getAsString();
            }
        }

        return null;
    }

    // Bearer Token을 Authorization 헤더에 포함시키는 메서드
    private HttpHeaders createHeaders(String accessToken) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }
}