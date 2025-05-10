package an.inhaintegration.icross.controller;

import an.inhaintegration.icross.service.SubjectService;
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
public class ICrossSubjectController {

    private final SubjectService subjectService;

    @GetMapping("/subjects")
    public String getSubjects(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        model.addAttribute("todaySubject", subjectService.findTodaySubjects(userDetails.getId()));
        model.addAttribute("allSubject", subjectService.findAllSubjects(userDetails.getId()));

        return "icross/home/subjectList";
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
