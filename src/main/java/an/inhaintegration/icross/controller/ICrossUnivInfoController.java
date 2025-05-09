package an.inhaintegration.icross.controller;

import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.service.UnivInfoService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/i-cross/univ-info")
public class ICrossUnivInfoController {

    private final UnivInfoService univInfoService;

    public ICrossUnivInfoController(UnivInfoService univInfoService) {
        this.univInfoService = univInfoService;
    }

    @GetMapping
    public String registerUnivInfo() {

        return "icross/registerUnivInfo";
    }

    @PostMapping
    public String register(@AuthenticationPrincipal CustomUserDetails userDetails,
                           @RequestParam("password") String password, Model model) {

        if(password == null || password.isEmpty()) {
            model.addAttribute("passwordError", "비밀번호를 입력하세요!");
            return "icross/registerUnivInfo";
        }

        univInfoService.create(userDetails.getId(), password);
    }
}
