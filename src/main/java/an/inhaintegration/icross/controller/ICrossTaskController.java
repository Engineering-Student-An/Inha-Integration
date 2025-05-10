package an.inhaintegration.icross.controller;

import an.inhaintegration.icross.service.TaskService;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.domain.Student;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/i-cross")
public class ICrossTaskController {

    private final TaskService taskService;

    @GetMapping("/tasks")
    public String tasks(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        model.addAttribute("remainAssignments", taskService.findInCompleteTasksByStudentIdAndIsVideo(userDetails.getId(), false));
        model.addAttribute("remainVideos", taskService.findInCompleteTasksByStudentIdAndIsVideo(userDetails.getId(), true));

        return "icross/home/remainTask";
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
