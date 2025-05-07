package an.inhaintegration.config;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Student loginStudent = userDetails.getStudent();

            // 학번이 비어있으면 stuId 입력 페이지로 리다이렉트
            if (loginStudent.getStuId() == null || loginStudent.getStuId().isBlank()) {
                response.sendRedirect("/oauth");
                return;
            }
        }

        // 리다이렉트 경로 지정
        response.sendRedirect("/home");
    }
}

