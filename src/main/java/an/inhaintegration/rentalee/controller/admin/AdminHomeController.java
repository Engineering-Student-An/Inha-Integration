package an.inhaintegration.rentalee.controller.admin;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminHomeController {

    @GetMapping()
    public String adminHome() {

        return "rentalee/admin/home";
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
