package an.inhaintegration.icross.controller;

import an.inhaintegration.rentalee.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/i-cross")
public class ICrossHomeController {

    private final StudentService studentService;

    @GetMapping("/index")
    public String ICrossIndex() {

        return "icross/index";
    }

//    @GetMapping
//    public String home(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        univInfoService.
//    }
}
