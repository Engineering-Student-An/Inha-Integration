package an.inhaintegration.config;

import an.inhaintegration.icross.domain.UnivInfo;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.icross.repository.UnivInfoRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class UnivInfoInterceptor implements HandlerInterceptor {

    private final UnivInfoRepository univInfoRepository;

    // I-CROSS 이용 시 /i-cross/index 를 제외하고 모든 /i-cross/** 요청에 대한 인터셉터
    // 만약 로그인한 회원의 id를 기반으로 한 UnivInfo가 없다면 등록하는 화면으로 리다이렉트 시킴
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws IOException {

        String requestURI = request.getRequestURI();

        // /i-cross/index 는 무시
        if (requestURI.equals("/i-cross/index")) {
            return true;
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            CustomUserDetails userDetails = (CustomUserDetails) principal;
            Long loginStudentId = userDetails.getId();

            Optional<UnivInfo> univInfoOpt = univInfoRepository.findByStudentId(loginStudentId);
            if (univInfoOpt.isPresent()) {
                return true;
            } else {
                response.sendRedirect("/i-cross/univ-info"); // UnivInfo 없으면 등록 페이지로 리다이렉트
                return false;
            }
        }

        return false;
    }
}
