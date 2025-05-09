package an.inhaintegration.icross.controller;

import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.service.UnivInfoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/i-cross/univ-info")
public class ICrossUnivInfoController {

    private final UnivInfoService univInfoService;

    public ICrossUnivInfoController(UnivInfoService univInfoService) {
        this.univInfoService = univInfoService;
    }

    @GetMapping
    public String registerUnivInfo() {

        return "icross/univinfo/registerUnivInfo";
    }

    @PostMapping
    public String register(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam("password") String password, Model model) {

        if(password == null || password.isEmpty()) {
            model.addAttribute("passwordError", "비밀번호를 입력하세요!");
            return "icross/univinfo/registerUnivInfo";
        }

        univInfoService.save(userDetails.getId(), password);

        model.addAttribute("errorMessage", "I-Class 비밀번호가 등록되었습니다!");
        model.addAttribute("nextUrl", "/i-cross/home");

        return "error/errorMessage";
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
