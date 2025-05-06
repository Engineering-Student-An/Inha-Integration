package an.inhaintegration.controller;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import an.inhaintegration.dto.LoginRequestDto;
import an.inhaintegration.dto.OauthUserRequestDto;
import an.inhaintegration.dto.UserRequestDto;
import an.inhaintegration.service.*;
import jakarta.servlet.http.HttpServletRequest;
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

        return "home/index";
    }

    @GetMapping("/home")
    public String home(@AuthenticationPrincipal CustomUserDetails userDetails,
                       Model model, HttpSession session) {

        session.setAttribute("previousPage", "/home");

        model.addAttribute("rentalList", rentalService.findMyRentalINGList((userDetails == null) ? null : userDetails.getId()));
        model.addAttribute("recentNotice", boardService.findRecentNotice());
        model.addAttribute("importantPosts", crawlingInhaEEService.importantPostParser());
        model.addAttribute("recentPosts", crawlingInhaEEService.recentPostParser());

        return "home/dashboard";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {

        model.addAttribute("loginRequestDto", new LoginRequestDto());

        return "home/login";
    }

    @GetMapping("/login/error")
    public String loginError(Model model) {

        model.addAttribute("errorMessage", "일치하는 회원 정보가 없습니다.\n로그인 정보를 확인해주세요.");
        model.addAttribute("nextUrl", "/login");

        return "error/errorMessage";
    }

    @GetMapping("/oauth")
    public String oauthUpdate(Model model) {

        model.addAttribute("oauthUserRequestDto", new OauthUserRequestDto());

        return "home/join/addInfoAfterOauth";
    }

    @PostMapping("/oauth")
    public String validateStuId(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @Valid @ModelAttribute OauthUserRequestDto oauthUserRequestDto,
                                BindingResult bindingResult, Model model, HttpServletRequest request) {

        feeStudentService.validateOauthFeeStudent(oauthUserRequestDto, bindingResult);    // oauth 로그인 후 학생회비 납부 여부 검증

        if (bindingResult.hasErrors()) return "home/join/addInfoAfterOauth";

        studentService.updateOauthInfo(userDetails.getId(), oauthUserRequestDto, request);

        model.addAttribute("errorMessage", "정보 입력이 완료되었습니다! 대시보드로 이동합니다.");
        model.addAttribute("nextUrl", "/home");

        return "error/errorMessage";
    }

    @GetMapping("/join")
    public String joinPage(Model model) {

        model.addAttribute("userRequestDto", new UserRequestDto());

        return "home/join/join";
    }

    @PostMapping("/join")
    public String join(@Valid @ModelAttribute UserRequestDto userRequestDto,
                       BindingResult bindingResult, HttpSession session, Model model) {

        studentService.validateJoin(userRequestDto, bindingResult);             // 회원가입 정보 검증
        feeStudentService.validateFeeStudent(userRequestDto, bindingResult);    // 학생회비 납부 여부 검증

        if (bindingResult.hasErrors()) return "home/join/join";

        model.addAttribute("userRequestDto.phoneNumber", userRequestDto.getPhoneNumber());
        session.setAttribute("userRequestDto", userRequestDto);

        return "home/join/joinMessage";
    }

    @GetMapping("/join/verify")
    public String verifyEmail(Model model, HttpSession session) {

        session.setAttribute("userRequestDto", (UserRequestDto) session.getAttribute("userRequestDto"));
        model.addAttribute("isSent", false);

        return "home/join/verifyEmail";
    }

    @PostMapping("/join/verify")
    public String sendEmail(@RequestParam("email") String email, HttpSession session, Model model) {

        session.setAttribute("userRequestDto", (UserRequestDto) session.getAttribute("userRequestDto"));

        if(emailService.validateEmail(email)) {
            model.addAttribute("emailError", "이메일 주소가 올바르지 않습니다!");
            return "home/join/verifyEmail";
        }

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/joinEmail");
        session.setAttribute("verifyCode", authCode);

        model.addAttribute("isSent", true);
        model.addAttribute("email", email);

        return "home/join/verifyEmail";
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
        studentService.join((UserRequestDto) session.getAttribute("userRequestDto"), email);

        model.addAttribute("errorMessage", "회원가입이 완료되었습니다! 로그인 페이지로 이동합니다.");
        model.addAttribute("nextUrl", "/login");

        return "error/errorMessage";
    }
//
//    @GetMapping("/findPassword/info")
//    public String findPasswordForm(Model model) {
//        model.addAttribute("infoForm", new InfoForm());
//
//        return "home/findPassword";
//    }
//
//    @PostMapping("/findPassword/info")
//    public String findPasswordInfo(@ModelAttribute("infoForm") InfoForm infoForm,
//                                   BindingResult bindingResult, HttpSession session) {
//
//        if(infoForm.getStuId() == null || infoForm.getStuId().isEmpty()) {
//            bindingResult.addError(new FieldError("infoForm",
//                    "stuId", "학번을 입력해주세요!"));
//        }
//        if(infoForm.getPhoneNumber() == null || infoForm.getPhoneNumber().isEmpty()) {
//            bindingResult.addError(new FieldError("infoForm",
//                    "phoneNumber", "전화번호를 입력해주세요!"));
//        }
//
//        Student findStudent = studentService.findStudent(infoForm.getStuId());
//        if(findStudent == null) {
//            bindingResult.addError(new FieldError("infoForm",
//                    "stuId", "일치하는 학번의 계정이 없습니다. 회원가입 해 주세요!"));
//        } else {
//            if(!infoForm.getPhoneNumber().equals(findStudent.getPhoneNumber())) {
//                bindingResult.addError(new FieldError("infoForm",
//                        "phoneNumber", "등록된 비밀번호와 다릅니다. 확인 후 입력해주세요!"));
//            }
//        }
//
//        if(bindingResult.hasErrors()) {
//            return "home/findPassword";
//        }
//
//        session.setAttribute("findStudent", findStudent);
//        return "redirect:/findPassword/email";
//    }
//
//    @GetMapping("/findPassword/email")
//    public String findPasswordEmailForm(HttpSession session, Model model) {
//        session.setAttribute("findStudent", session.getAttribute("findStudent"));
//        model.addAttribute("emailForm", new EmailForm());
//        model.addAttribute("isEmailChecked", false);
//        model.addAttribute("isEmailSent", false);
//        return "home/findPassword_email";
//    }
//
//    @PostMapping("/findPassword/email")
//    public String findPasswordEmail(@ModelAttribute EmailForm emailForm, Model model, HttpSession session) {
//        Student findStudent = (Student) session.getAttribute("findStudent");
//
//        if(!emailForm.getEmail().equals(findStudent.getEmail())) {
//            model.addAttribute("errorMessage", "등록된 이메일과 동일하지 않습니다!");
//            model.addAttribute("nextUrl", "/findPassword/email");
//
//            return "error/errorMessage";
//        }
//        model.addAttribute("isEmailChecked", false);
//        model.addAttribute("isEmailSent", true);
//        model.addAttribute("emailForm", emailForm);
//
//        String authCode = emailService.createVerifyCode();
//        emailService.sendEmail(emailForm.getEmail(), authCode, "email/passwordEmail");
//
//        session.setAttribute("verifyCode", authCode);
//        session.setAttribute("findStudent", findStudent);
//
//        return "home/findPassword_email";
//    }
//
//    @PostMapping("/findPassword/email/verify")
//    public String verifyEmailCode(@RequestParam("code") String code, HttpSession session, Model model) {
//
//        String verifyCode = (String) session.getAttribute("verifyCode");
//
//        if(!code.equals(verifyCode)) {
//            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다!");
//            model.addAttribute("nextUrl", "/findPassword/email");
//            return "error/errorMessage";
//        }
//
//        session.setAttribute("findStudent", session.getAttribute("findStudent"));
//        model.addAttribute("errorMessage", "인증 문자가 확인되었습니다.\n 비밀번호 재설정 페이지로 넘어갑니다!");
//        model.addAttribute("nextUrl", "/findPassword/reset");
//        return "error/errorMessage";
//
//    }
//
//    @GetMapping("/findPassword/reset")
//    public String resetPasswordForm(HttpSession session, Model model) {
//        model.addAttribute("request", new ChangePasswordRequest());
//        session.setAttribute("findStudent", session.getAttribute("findStudent"));
//        return "home/resetPassword";
//    }
//
//    @PostMapping("/findPassword/reset")
//    public String resetPassword(@ModelAttribute("request") ChangePasswordRequest request, BindingResult bindingResult, HttpSession session, Model model) {
//
//        if(request.getChangePassword().isEmpty()) {
//            bindingResult.addError(new FieldError("request",
//                    "changePassword", "비밀번호를 입력해주세요!"));
//        }
//        if(request.getChangePasswordCheck().isEmpty()) {
//            bindingResult.addError(new FieldError("request",
//                    "changePasswordCheck", "비밀번호를 한번 더 입력해주세요!"));
//        }
//        if(!request.getChangePassword().equals(request.getChangePasswordCheck())) {
//            bindingResult.addError(new FieldError("request",
//                    "changePassword", "비밀번호가 일치하지 않습니다!"));
//            bindingResult.addError(new FieldError("request",
//                    "changePasswordCheck", "비밀번호가 일치하지 않습니다!"));
//        }
//
//        if(bindingResult.hasErrors()) {
//            return "home/resetPassword";
//        }
//
//        Student student = (Student) session.getAttribute("findStudent");
//        studentService.changePassword(student.getStuId(), request);
//
//        model.addAttribute("errorMessage", "비밀번호가 변경되었습니다. 로그인을 진행해 주세요!");
//        model.addAttribute("nextUrl", "/login");
//        return "error/errorMessage";
//    }

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