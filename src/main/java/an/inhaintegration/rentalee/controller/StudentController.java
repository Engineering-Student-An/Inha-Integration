package an.inhaintegration.rentalee.controller;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.student.ChangePasswordRequestDto;
import an.inhaintegration.rentalee.dto.student.StudentInfoRequestDto;
import an.inhaintegration.rentalee.service.EmailService;
import an.inhaintegration.rentalee.service.StudentService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;
    private final EmailService emailService;

    @GetMapping("/my-page")
    public String changeInfo() {

        return "rentalee/student/myPage";
    }

    @GetMapping("/my-page/password/verify")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {

        // Oauth 로그인 시 사용 불가능
        if(userDetails.getStudent().getProvider() != null) {
            model.addAttribute("errorMessage", "소셜 로그인은 비밀번호 변경이 불가합니다!");
            model.addAttribute("nextUrl", "/my-page");
            return "error/errorMessage";
        }

        model.addAttribute("isPasswordChecked", false);
        model.addAttribute("isEmailChecked", false);
        model.addAttribute("isEmailSent", false);

        return "rentalee/student/beforeChangePassword";
    }

    @PostMapping("/my-page/password/verify")
    public String changePassword(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestParam("password") String password, Model model){

        // 현재 비밀번호와 다른 경우
        if(!studentService.passwordCheck(userDetails.getId(), password)) {
            model.addAttribute("errorMessage", "현재 비밀번호와 동일하지 않습니다!");
            model.addAttribute("nextUrl", "/my-page/password/verify");

            return "error/errorMessage";
        }

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", false);
        model.addAttribute("isEmailChecked", false);

        return "rentalee/student/beforeChangePassword";
    }

    @PostMapping("/my-page/password/verify/email")
    public String verifyEmail(@RequestParam("email") String email, HttpSession httpSession, Model model) {

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", true);
        model.addAttribute("isEmailChecked", false);

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/passwordEmail");

        httpSession.setAttribute("verifyCode", authCode);

        return "rentalee/student/beforeChangePassword";
    }

    @PostMapping("/my-page/password/verify/email/code")
    public String verifyEmailCode(@RequestParam("code") String code, HttpSession httpSession, Model model) {

        String verifyCode = (String) httpSession.getAttribute("verifyCode");
        if (!code.equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다!");
            model.addAttribute("nextUrl", "/my-page/password/verify");
            return "error/errorMessage";
        }

        model.addAttribute("isPasswordChecked", true);
        model.addAttribute("isEmailSent", true);
        model.addAttribute("isEmailChecked", true);

        return "rentalee/student/beforeChangePassword";
    }

    @GetMapping("/my-page/password")
    public String changePasswordNext(Model model) {

        model.addAttribute("changePasswordRequestDto", new ChangePasswordRequestDto());

        return "rentalee/student/changePassword";
    }

    @PostMapping("/my-page/password")
    public String changePasswordForm(@AuthenticationPrincipal CustomUserDetails userDetails,
                                     @Valid @ModelAttribute("changePasswordRequestDto") ChangePasswordRequestDto changePasswordRequestDto,
                                     BindingResult bindingResult, Model model) {

        studentService.validateChangePassword(userDetails.getId(), changePasswordRequestDto, bindingResult);

        if(bindingResult.hasErrors()) {
            return "rentalee/student/changePassword";
        }

        studentService.changePassword(userDetails.getId(),changePasswordRequestDto);

        model.addAttribute("errorMessage", "비밀번호가 변경되었습니다!");
        model.addAttribute("nextUrl", "/my-page");
        return "error/errorMessage";
    }

    @GetMapping("/my-page/email")
    public String changeEmail(Model model) {

        model.addAttribute("isSent", false);

        return "rentalee/student/changeEmail";
    }

    @PostMapping("/my-page/email")
    public String changeEmailSend(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestParam("email") String email, Model model, HttpServletRequest request) {

        String errorMessage = studentService.validateEmail(userDetails.getId(), email);
        if(errorMessage != null) {
            model.addAttribute("emailError", errorMessage);
            return "rentalee/student/changeEmail";
        }

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/emailChangeEmail");

        request.getSession().setAttribute("verifyCode", authCode);
        model.addAttribute("email", email);
        model.addAttribute("isSent", true);

        return "rentalee/student/changeEmail";
    }

    @PostMapping("/my-page/email/verify")
    public String verifyCodeCheck(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @RequestParam("email") String email,
                                  @RequestParam("code") String code,
                                  HttpSession session, Model model) {

        // 메일 보낸 인증 문자
        String verifyCode = (String) session.getAttribute("verifyCode");

        // 인증 문자와 동일한지 검증
        if(!code.equals(verifyCode)) {
            model.addAttribute("errorMessage", "인증 문자가 일치하지 않습니다! 다시 시도해주세요!");
            model.addAttribute("nextUrl", "/my-page/email");
            return "error/errorMessage";
        }

        studentService.editEmail(userDetails.getId(), email);

        model.addAttribute("errorMessage", "이메일이 변경되었습니다!");
        model.addAttribute("nextUrl", "/my-page");
        return "error/errorMessage";

    }

    @GetMapping("/password")
    public String findPasswordForm(Model model) {

        model.addAttribute("studentInfoRequestDto", new StudentInfoRequestDto());

        return "rentalee/home/findPassword";
    }

    @PostMapping("/password")
    public String findPasswordInfo(@ModelAttribute("studentInfoRequestDto") StudentInfoRequestDto studentInfoRequestDto,
                                   BindingResult bindingResult, HttpSession session) {

        // 회원 정보 검증
        studentService.checkStudentInfo(studentInfoRequestDto, bindingResult);

        if(bindingResult.hasErrors()) return "rentalee/home/findPassword";

        // 비밀번호 초기화하려는 회원 id 세션에 저장
        studentService.setStudentIdToSession(studentInfoRequestDto.getStuId(), session);

        return "redirect:/password/email";
    }

    @GetMapping("/password/email")
    public String findPasswordEmailForm(Model model) {

        model.addAttribute("isEmailChecked", false);
        model.addAttribute("isEmailSent", false);

        return "rentalee/home/findPassword_email";
    }

    @PostMapping("/password/email")
    public String findPasswordEmail(@RequestParam("email") String email, Model model, HttpSession session) {

        Long studentId = (Long) session.getAttribute("studentId");

        String errorMessage = studentService.checkEmail(studentId, email);
        if(errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("nextUrl", "/password/email");
            return "error/errorMessage";
        }

        model.addAttribute("isEmailSent", true);
        model.addAttribute("isEmailChecked", false);

        String authCode = emailService.createVerifyCode();
        emailService.sendEmail(email, authCode, "email/passwordEmail");

        session.setAttribute("verifyCode", authCode);

        return "rentalee/home/findPassword_email";
    }

    @PostMapping("/password/email/code")
    public String verifyEmailCodeResetPassword(@RequestParam("code") String code, HttpSession session, Model model) {

        String verifyCode = (String) session.getAttribute("verifyCode");

        model.addAttribute("errorMessage", (!code.equals(verifyCode)) ? "인증 문자가 일치하지 않습니다!" : "인증 문자가 확인되었습니다.\n 비밀번호 재설정 페이지로 이동합니다!");
        model.addAttribute("nextUrl", (!code.equals(verifyCode)) ? "/password/email" : "/password/reset");

        return "error/errorMessage";
    }

    @GetMapping("/password/reset")
    public String resetPasswordForm(Model model) {

        model.addAttribute("changePasswordRequestDto", new ChangePasswordRequestDto());

        return "rentalee/home/resetPassword";
    }

    @PostMapping("/password/reset")
    public String resetPassword(@ModelAttribute("changePasswordRequestDto") ChangePasswordRequestDto changePasswordRequestDto,
                                BindingResult bindingResult, HttpSession session, Model model) {

        Long studentId = (Long) session.getAttribute("studentId");

        studentService.validateChangePassword(studentId, changePasswordRequestDto, bindingResult);

        if(bindingResult.hasErrors()) return "rentalee/home/resetPassword";

        studentService.changePassword(studentId, changePasswordRequestDto);

        model.addAttribute("errorMessage", "비밀번호가 변경되었습니다. 로그인을 진행해 주세요!");
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
}