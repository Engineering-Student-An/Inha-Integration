package an.inhaintegration.controller.admin;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import an.inhaintegration.service.BoardService;
import an.inhaintegration.service.StudentService;
import jakarta.servlet.http.HttpSession;
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

    private final BoardService boardService;
    private final StudentService studentService;

    @GetMapping({"/", ""})
    public String adminHome(HttpSession session) {

        return "admin/home";
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
