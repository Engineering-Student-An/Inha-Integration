package an.inhaintegration.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class MobileInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();

        String userAgent = request.getHeader("User-Agent").toUpperCase();
        boolean isMobileUser = userAgent.contains("ANDROID") || userAgent.contains("TABLET") ||
                userAgent.contains("IPAD") || userAgent.contains("MOBILE") ||
                userAgent.contains("IPHONE");


        if(isMobileUser) {
            session.setAttribute("isMobile", true);
        } else {
            session.setAttribute("isMobile", false);
        }

        return true;
    }
}
