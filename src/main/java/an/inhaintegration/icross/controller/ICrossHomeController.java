package an.inhaintegration.icross.controller;

import an.inhaintegration.icross.domain.Schedule;
import an.inhaintegration.icross.service.KakaoMessageService;
import an.inhaintegration.icross.service.ScheduleService;
import an.inhaintegration.icross.service.ScheduledTask;
import an.inhaintegration.icross.service.WeatherService;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.domain.Student;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@RequestMapping("/i-cross")
public class ICrossHomeController {

    private final WeatherService weatherService;
    private final ScheduleService scheduleService;
    private final KakaoMessageService kakaoMessageService;
    private final ScheduledTask scheduledTask;

    @GetMapping("/index")
    public String ICrossIndex() {

        return "icross/home/index";
    }

    @GetMapping("/home")
    public String loginIClass(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return "icross/home/loginICross";
    }

    @GetMapping
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails,
                       HttpServletRequest request, Model model) {

        // 스케줄 추가
        Long studentId = userDetails.getId();
        List<Schedule> schedules = Optional.of(scheduleService.findByStudentId(studentId))
                .filter(list -> !list.isEmpty())
                .orElseGet(() -> {
                    scheduleService.createSchedule(studentId);
                    return scheduleService.findByStudentId(studentId);
                });
        model.addAttribute("scheduleList", schedules);


        // 스케줄 -> 카톡 보내기
        String code = request.getParameter("code");
        if(code != null && kakaoMessageService.getKakaoAuthToken(code)) {
            kakaoMessageService.sendMyMessage(userDetails.getId());
            return "redirect:/i-cross";
        }

        // 인하대학교 날씨 추가
        model.addAttribute("weather", weatherService.returnWeather());

        return "icross/home/home";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getStudent();
        }

        return null;
    }
}
