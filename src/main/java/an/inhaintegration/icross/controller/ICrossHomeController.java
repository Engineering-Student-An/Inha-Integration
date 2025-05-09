package an.inhaintegration.icross.controller;

import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/i-cross")
public class ICrossHomeController {

    private final StudentService studentService;

    @GetMapping("/index")
    public String ICrossIndex() {

        return "icross/home/index";
    }

    @GetMapping("/home")
    public String loginIClass(@AuthenticationPrincipal CustomUserDetails userDetails) {

        return "icross/home/loginICross";
    }

    @GetMapping
    public String home() {

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
