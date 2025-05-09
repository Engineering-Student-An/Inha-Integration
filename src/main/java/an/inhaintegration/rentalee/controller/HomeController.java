package an.inhaintegration.rentalee.controller;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.student.LoginRequestDto;
import an.inhaintegration.rentalee.dto.student.StudentOauthRequestDto;
import an.inhaintegration.rentalee.dto.student.StudentRequestDto;
import an.inhaintegration.rentalee.service.*;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/")
public class HomeController {

    private final StudentService studentService;
    private final BoardService boardService;
    private final FeeStudentService feeStudentService;
    private final RentalService rentalService;
    private final CrawlingInhaEEService crawlingInhaEEService;
    private final EmailService emailService;

    @GetMapping(value = {"", "/"})
    public String index() {

        return "rentalee/home/index";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails,
                       Model model, HttpSession session) {

        session.setAttribute("previousPage", "/home");

        model.addAttribute("rentalList", rentalService.findMyRentalINGList((userDetails == null) ? null : userDetails.getId()));
        model.addAttribute("recentNotice", boardService.findRecentNotice());
        model.addAttribute("importantPosts", crawlingInhaEEService.importantPostParser());
        model.addAttribute("recentPosts", crawlingInhaEEService.recentPostParser());

        return "rentalee/home/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {

        model.addAttribute("loginRequestDto", new LoginRequestDto());

        return "rentalee/home/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {

        model.addAttribute("errorMessage", "일치하는 회원 정보가 없습니다.\n로그인 정보를 확인해주세요.");
        model.addAttribute("nextUrl", "/login");

        return "error/errorMessage";
    }

    @GetMapping("/oauth")
    public String oauthUpdate(Model model) {

        model.addAttribute("studentOauthRequestDto", new StudentOauthRequestDto());

        return "rentalee/home/join/addInfoAfterOauth";
    }

    @PostMapping("/oauth")
    public String validateStuId(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @Valid @ModelAttribute("studentOauthRequestDto") StudentOauthRequestDto studentOauthRequestDto,
                                BindingResult bindingResult, Model model) {

        feeStudentService.validateOauthFeeStudent(studentOauthRequestDto, bindingResult);    // oauth 로그인 후 학생회비 납부 여부 검증

        if (bindingResult.hasErrors()) return "rentalee/home/join/addInfoAfterOauth";

        studentService.updateOauthInfo(userDetails.getId(), studentOauthRequestDto);

        model.addAttribute("errorMessage", "정보 입력이 완료되었습니다! 대시보드로 이동합니다.");
        model.addAttribute("nextUrl", "/home");

        return "error/errorMessage";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {

        model.addAttribute("studentRequestDto", new StudentRequestDto());

        return "rentalee/home/join/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute("studentRequestDto") StudentRequestDto studentRequestDto,
                       BindingResult bindingResult, HttpSession session, Model model) {

        studentService.validateJoin(studentRequestDto, bindingResult);             // 회원가입 정보 검증
        feeStudentService.validateFeeStudent(studentRequestDto, bindingResult);    // 학생회비 납부 여부 검증

        if (bindingResult.hasErrors()) return "rentalee/home/join/join";

        session.setAttribute("studentRequestDto", studentRequestDto);

        return "rentalee/home/join/joinMessage";
    }

    @GetMapping("/join/verify")
    public String verifyEmail(Model model, HttpSession session) {

        session.setAttribute("studentRequestDto", (StudentRequestDto) session.getAttribute("studentRequestDto"));
        model.addAttribute("isSent", false);

        return "rentalee/home/join/verifyEmail";
    }

    @PostMapping("/join/verify")
    public String sendEmail(@RequestParam("email") String email, HttpSession session, Model model) {

        session.setAttribute("studentRequestDto", (StudentRequestDto) session.getAttribute("studentRequestDto"));

        if(emailService.validateEmail(email)) {
            model.addAttribute("emailError", "이메일 주소가 올바르지 않습니다!");
            return "rentalee/home/join/verifyEmail";
        }

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/joinEmail");
        session.setAttribute("verifyCode", authCode);

        model.addAttribute("isSent", true);
        model.addAttribute("email", email);

        return "rentalee/home/join/verifyEmail";
    }

    @PostMapping("/join/verify/code")
    public String verifyCodeCheck(@RequestParam("email") String email,
                                  @RequestParam("inputCode") String inputCode,
                                  HttpSession session, Model model) {

        // 메일 보낸 인증 문자
        String verifyCode = (String) session.getAttribute("verifyCode");

        // 인증 문자와 동일한지 검증
        if(!inputCode.equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/join/verify");
            return "error/errorMessage";
        }

        // 인증 문자 동일하면 회원가입
        studentService.join((StudentRequestDto) session.getAttribute("studentRequestDto"), email);

        model.addAttribute("errorMessage", "회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.");
        model.addAttribute("nextUrl", "/login");

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

    @ModelAttribute("isMobile")
    public boolean isMobile(HttpSession session) {
        return (boolean) session.getAttribute("isMobile");
    }
}