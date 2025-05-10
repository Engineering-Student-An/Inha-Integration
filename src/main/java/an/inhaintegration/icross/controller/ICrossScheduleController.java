package an.inhaintegration.icross.controller;

import an.inhaintegration.icross.dto.ScheduleRequestDto;
import an.inhaintegration.icross.service.ScheduleService;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/i-cross")
public class ICrossScheduleController {

    private final ScheduleService scheduleService;

    @DeleteMapping("/schedules")
    public String recreateSchedule(@AuthenticationPrincipal CustomUserDetails userDetails) {

        scheduleService.clearSchedule(userDetails.getId());

        return "redirect:/i-cross";
    }

    @GetMapping("/schedule")
    public String createScheduleForm(Model model) {

        model.addAttribute("scheduleRequestDto", new ScheduleRequestDto());

        return "icross/schedule/createSchedule";
    }

    @PostMapping("/schedule")
    public String createSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @ModelAttribute("scheduleRequestDto") ScheduleRequestDto scheduleRequestDto) {

        scheduleService.save(userDetails.getId(), scheduleRequestDto);

        return "redirect:/i-cross";
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
