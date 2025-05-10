package an.inhaintegration.icross.service;

import an.inhaintegration.icross.domain.Schedule;
import an.inhaintegration.icross.domain.UnivInfo;
import an.inhaintegration.icross.dto.KakaoMessageRequestDto;
import an.inhaintegration.icross.exception.UnivInfoNotFoundException;
import an.inhaintegration.icross.repository.ScheduleRepository;
import an.inhaintegration.icross.repository.UnivInfoRepository;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;

@Service
@RequiredArgsConstructor
public class KakaoMessageService extends HttpCallService {

    private final UnivInfoRepository univInfoRepository;
    private final ScheduleRepository scheduleRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String AUTH_URL = "https://kauth.kakao.com/oauth/token";
    private static final String MSG_SEND_SERVICE_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private static final String SEND_SUCCESS_MSG = "메시지 전송에 성공했습니다.";
    private static final String SEND_FAIL_MSG = "메시지 전송에 실패했습니다.";
    private static final String SUCCESS_CODE = "0"; //kakao api에서 return해주는 success code 값

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String KAKAO_CLIENT_ID;

    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String KAKAO_CLIENT_SECRET;

    @Value("${}")
    public static String authToken;

    public boolean getKakaoAuthToken(String code) {

        HttpHeaders header = new HttpHeaders();
        String accessToken = "";
        String refreshToken = "";
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();

        header.set("Content-Type", APP_TYPE_URL_ENCODED);

        System.out.println("KAKAO_CLIENT_ID = " + KAKAO_CLIENT_ID);
        System.out.println("KAKAO_CLIENT_SECRET = " + KAKAO_CLIENT_SECRET);
        parameters.add("code", code);
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", KAKAO_CLIENT_ID);
        parameters.add("client_secret", KAKAO_CLIENT_SECRET);
        parameters.add("redirect_uri", "http://localhost:8080/i-cross");
        parameters.add("scope", "talk_message"); // 필요한 권한을 요청하는 부분 추가


        HttpEntity<?> requestEntity = httpClientEntity(header, parameters);

        ResponseEntity<String> response = httpRequest(AUTH_URL, HttpMethod.POST, requestEntity);
        System.out.println("응답 코드: " + response.getStatusCodeValue());
        System.out.println("응답 내용: " + response.getBody());

        JSONObject jsonData = new JSONObject(response.getBody());
        System.out.println("jsonData = " + jsonData);
        accessToken = jsonData.get("access_token").toString();
        refreshToken = jsonData.get("refresh_token").toString();

        System.out.println("accessToken = " + accessToken);
        System.out.println("refreshToken = " + refreshToken);
        if (accessToken.isEmpty() || refreshToken.isEmpty()) {
            logger.debug("토큰발급에 실패했습니다.");
            return false;
        } else {
            authToken = accessToken;
            return true;
        }
    }

    public boolean sendMyMessage(Long studentId) {

        UnivInfo univInfo = univInfoRepository.findByStudentId(studentId).orElseThrow(UnivInfoNotFoundException::new);

        List<Schedule> schedules = scheduleRepository.findSchedulesByUnivInfoIdOrderByTime(univInfo.getId());

        StringBuilder sb = new StringBuilder();
        for (Schedule schedule : schedules) {
            sb.append(schedule.getTime()).append(" ").append(schedule.getContent()).append("\n\n");
        }

        KakaoMessageRequestDto kakaoMessageRequestDto = new KakaoMessageRequestDto();
        kakaoMessageRequestDto.setBtnTitle("자세히보기");
        kakaoMessageRequestDto.setMobileUrl("");
        kakaoMessageRequestDto.setObjType("text");
        kakaoMessageRequestDto.setWebUrl("");
        kakaoMessageRequestDto.setText(sb.toString());

        String accessToken = authToken;

        return sendKakaoMessage(accessToken, kakaoMessageRequestDto);
    }

    public boolean sendKakaoMessage(String accessToken, KakaoMessageRequestDto kakaoMessageRequestDto) {

        JSONObject linkObj = new JSONObject();
        linkObj.put("web_url", kakaoMessageRequestDto.getWebUrl());
        linkObj.put("mobile_web_url", kakaoMessageRequestDto.getMobileUrl());

        JSONObject templateObj = new JSONObject();
        templateObj.put("object_type", kakaoMessageRequestDto.getObjType());
        templateObj.put("text", kakaoMessageRequestDto.getText());
        templateObj.put("link", linkObj);
        templateObj.put("button_title", kakaoMessageRequestDto.getBtnTitle());

        HttpHeaders header = new HttpHeaders();
        header.set("Content-Type", APP_TYPE_URL_ENCODED);
        header.set("Authorization", "Bearer " + accessToken);

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("template_object", templateObj.toString());

        HttpEntity<?> messageRequestEntity = httpClientEntity(header, parameters);

        String resultCode = "";
        ResponseEntity<String> response = httpRequest(MSG_SEND_SERVICE_URL, HttpMethod.POST, messageRequestEntity);
        JSONObject jsonData = new JSONObject(response.getBody());
        resultCode = jsonData.get("result_code").toString();

        return successCheck(resultCode);
    }

    public boolean successCheck(String resultCode) {
        if(resultCode.equals(SUCCESS_CODE)) {
            logger.info(SEND_SUCCESS_MSG);
            return true;
        }else {
            logger.debug(SEND_FAIL_MSG);
            return false;
        }

    }

    private HttpEntity<?> httpClientEntity(HttpHeaders headers, MultiValueMap<String, String> parameters) {
        return new HttpEntity<>(parameters, headers);
    }
}
