package an.inhaintegration.config;

import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.domain.Student;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2AuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private RequestCache requestCache = new HttpSessionRequestCache();

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException{

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Student loginStudent = userDetails.getStudent();

            // 학번이 비어있으면 stuId 입력 페이지로 리다이렉트
            if (loginStudent.getStuId() == null || loginStudent.getStuId().isBlank()) {
                response.sendRedirect((loginStudent.getProvider().equals("google")) ? "/oauth?studentId=" + loginStudent.getId() : "/oauth");
                return;
            }

            if(loginStudent.getProvider().equals("google")) {
                response.sendRedirect("/home/" + loginStudent.getId());
                return;
            }
        }

        SavedRequest savedRequest = requestCache.getRequest(request, response);

        if(savedRequest != null){
            String targetUrl = savedRequest.getRedirectUrl();

            if(targetUrl.contains("like")) {

                String[] parts = targetUrl.split("/");
                String boardId = parts[4];
                response.sendRedirect("/board/" + boardId);
            }
            else {
                response.sendRedirect(targetUrl);
            }
        } else {
            response.sendRedirect("/home");
        }
    }
}

