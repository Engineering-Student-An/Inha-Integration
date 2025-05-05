package an.inhaintegration.config;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomOauth2UserDetails;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        // loginStudent 를 세션에 저장
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomOauth2UserDetails) {
            CustomOauth2UserDetails userDetails = (CustomOauth2UserDetails) principal;
            Student loginStudent = userDetails.getStudent();

            HttpSession session = request.getSession();
            session.setAttribute("loginStudent", loginStudent);
        }

        // 리다이렉트 경로 지정
        response.sendRedirect("/home");
    }
}

