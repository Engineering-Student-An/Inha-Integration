package an.inhaintegration.rentalee.service;

import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.dto.student.ChangePasswordRequestDto;
import an.inhaintegration.rentalee.dto.student.StudentInfoRequestDto;
import an.inhaintegration.rentalee.dto.student.StudentOauthRequestDto;
import an.inhaintegration.rentalee.dto.student.StudentRequestDto;
import an.inhaintegration.rentalee.exception.StudentNotFoundException;
import an.inhaintegration.rentalee.repository.StudentRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 학번 중복 검증 메서드
    public boolean checkStuIdDuplicate(String stuId){

        return studentRepository.existsByStuId(stuId);
    }

    // 회원가입 검증 메서드
    public void validateJoin(@Valid StudentRequestDto studentRequestDto, BindingResult bindingResult) {

        if (checkStuIdDuplicate(studentRequestDto.getStuId())) {
            bindingResult.addError(new FieldError("studentRequestDto",
                    "stuId", "이미 가입되어 있습니다!"));
        }

        if (!studentRequestDto.getPassword().equals(studentRequestDto.getPasswordCheck())) {
            bindingResult.addError(new FieldError("studentRequestDto",
                    "passwordCheck", "비밀번호가 동일하지 않습니다!"));
        }

        if (!Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$").matcher(studentRequestDto.getPhoneNumber()).matches()) {
            bindingResult.addError(new FieldError("studentRequestDto",
                    "phoneNumber", "전화번호 형식이 올바르지 않습니다!"));
        }
    }

    // 회원가입
    @Transactional
    public void join(StudentRequestDto studentRequestDto, String email){

        String encodedPassword = bCryptPasswordEncoder.encode(studentRequestDto.getPassword());

        Student newStudent = Student.builder()
                .loginId(studentRequestDto.getLoginId())
                .stuId(studentRequestDto.getStuId())
                .name(studentRequestDto.getName())
                .password(encodedPassword)
                .phoneNumber(studentRequestDto.getPhoneNumber())
                .role(studentRequestDto.getRole())
                .email(email)
                .build();

        studentRepository.save(newStudent);
    }

    @Transactional
    public void updateOauthInfo(Long studentId, StudentOauthRequestDto studentOauthRequestDto) {

        // 로그인한 회원 조회
        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        // 추가적인 정보 업데이트
        loginStudent.addInfoAfterOauth(studentOauthRequestDto.getStuId(), studentOauthRequestDto.getName(), studentOauthRequestDto.getPhoneNumber());

        // SecurityContextHolder에 반영
        SecurityContextHolder.getContext().setAuthentication(getAuthenticationToken(loginStudent));
    }

    @Transactional
    public void updateOauthInfo(HttpServletRequest request, StudentOauthRequestDto studentOauthRequestDto) {

        // 쿠키에서 studentId 꺼내기
        Cookie[] cookies = request.getCookies();
        Long studentId = 0L;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("googleLoginId")) {
                    studentId = Long.parseLong(cookie.getValue());
                    break;
                }
            }
        }

        // 로그인한 회원 조회
        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        // 추가적인 정보 업데이트
        loginStudent.addInfoAfterOauth(studentOauthRequestDto.getStuId(), studentOauthRequestDto.getName(), studentOauthRequestDto.getPhoneNumber());

        // SecurityContextHolder에 반영
        SecurityContextHolder.getContext().setAuthentication(getAuthenticationToken(loginStudent));
    }

    // 비밀번호 검증 메서드
    public boolean passwordCheck(Long studentId, String password) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.matches(password, loginStudent.getPassword());
    }

    public void validateChangePassword(Long studentId, @Valid ChangePasswordRequestDto changePasswordRequestDto, BindingResult bindingResult) {

        // 현재 비밀번호와 동일하게 변경하려는 경우
        if(passwordCheck(studentId, changePasswordRequestDto.getChangePassword())){
            bindingResult.addError(new FieldError("changePasswordRequestDto",
                    "changePassword", "현재 비밀번호와 동일하게 변경 불가합니다!"));
        }
        // 비밀번호 체크와 동일하지 않은 경우
        else if (!changePasswordRequestDto.getChangePassword().equals(changePasswordRequestDto.getChangePasswordCheck())) {
            bindingResult.addError(new FieldError("changePasswordRequestDto",
                    "changePassword", "비밀번호가 동일하지 않습니다!"));
            bindingResult.addError(new FieldError("changePasswordRequestDto",
                    "changePasswordCheck", "비밀번호가 동일하지 않습니다!"));
        }
    }

    // 비밀번호 변경
    @Transactional
    public void changePassword(Long studentId, ChangePasswordRequestDto changePasswordRequestDto) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        loginStudent.editPassword(bCryptPasswordEncoder.encode(changePasswordRequestDto.getChangePassword()));
    }

    public String validateEmail(Long studentId, String inputEmail) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        String email = loginStudent.getEmail();

        if(inputEmail.equals(email)) return "현재 이메일 주소와 동일합니다!";    // 현재 이메일과 동일한 경우
        if(inputEmail.isEmpty()) return "이메일 주소를 입력해주세요!";               // 입력이 없는 경우
        if(!inputEmail.contains("@") || !inputEmail.contains(".")) return "이메일 주소가 올바르지 않습니다!";   // 이메일 형식이 아닌 경우

        // 이메일 검증 완료 시 null 반환
        return null;
    }

    @Transactional
    public void editEmail(Long studentId, String email) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        loginStudent.editEmail(email);
    }

    // 기존의 인증 정보를 변경하는 메서드
    private static OAuth2AuthenticationToken getAuthenticationToken(Student loginStudent) {

        // 기존 인증 정보 가져오기
        OAuth2AuthenticationToken currentAuth = (OAuth2AuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

        // 통합된 Principal 클래스 사용
        CustomUserDetails updatedPrincipal = new CustomUserDetails(loginStudent, currentAuth.getPrincipal().getAttributes());

        // 새 Authentication 생성
        OAuth2AuthenticationToken newAuth = new OAuth2AuthenticationToken(
                updatedPrincipal,
                updatedPrincipal.getAuthorities(),
                currentAuth.getAuthorizedClientRegistrationId()
        );

        return newAuth;
    }

    // 비밀번호 초기화 전 개인 정보 검증 메서드
    public void checkStudentInfo(StudentInfoRequestDto studentInfoRequestDto, BindingResult bindingResult) {

        // 학번이 비어있는 경우
        if(studentInfoRequestDto.getStuId() == null || studentInfoRequestDto.getStuId().isEmpty()) {
            bindingResult.addError(new FieldError("studentInfoRequestDto", "stuId", "학번을 입력해주세요!"));
        }

        // 이름이 비어있는 경우
        if(studentInfoRequestDto.getName() == null || studentInfoRequestDto.getName().isEmpty()) {
            bindingResult.addError(new FieldError("studentInfoRequestDto", "name", "이름을 입력해주세요!"));
        }

        // 전화번호가 비어있는 경우
        if(studentInfoRequestDto.getPhoneNumber() == null || studentInfoRequestDto.getPhoneNumber().isEmpty()) {
            bindingResult.addError(new FieldError("studentInfoRequestDto", "phoneNumber", "전화번호를 입력해주세요!"));
        }

        if(bindingResult.hasErrors()) return;

        Student foundStudent = studentRepository.findByStuId(studentInfoRequestDto.getStuId()).orElse(null);

        // 회원이 없는 경우
        if(foundStudent == null) {
            bindingResult.addError(new FieldError("studentInfoRequestDto", "stuId", "일치하는 학번의 계정이 없습니다. 회원가입 해 주세요!"));
        } else if(!studentInfoRequestDto.getName().equals(foundStudent.getName())) {
            bindingResult.addError(new FieldError("studentInfoRequestDto", "name", "이름을 확인해주세요!"));
        } else if(!studentInfoRequestDto.getPhoneNumber().equals(foundStudent.getPhoneNumber())) {
            bindingResult.addError(new FieldError("studentInfoRequestDto", "phoneNumber", "전화번호를 확인해주세요!"));
        } else if(foundStudent.getProvider() != null) { // Oauth 로그인 회원인 경우
            String providerInKorean = switch (foundStudent.getProvider()) {
                case "google" -> "구글";
                case "kakao" -> "카카오";
                case "naver" -> "네이버";
                case "github" -> "깃허브";
                default -> null;
            };
            bindingResult.addError(new FieldError("studentInfoRequestDto", "stuId", "해당 계정은 " + providerInKorean + " (소셜 로그인)으로 가입되었습니다!"));
        }
    }

    public String checkEmail(Long studentId, String email) {

        if(email.isEmpty()) return "이메일 주소를 입력해주세요!";               // 입력이 없는 경우
        if(!email.contains("@") || !email.contains(".")) return "이메일 주소가 올바르지 않습니다!";   // 이메일 형식이 아닌 경우

        Student foundStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        if(!email.equals(foundStudent.getEmail())) return "이메일 주소가 동일하지 않습니다! 확인 후 다시 입력해주세요!";    // 이메일 주소가 다른 경우
        return null;
    }

    public void setStudentIdToSession(String stuId, HttpSession session) {

        Student student = studentRepository.findByStuId(stuId).orElseThrow(StudentNotFoundException::new);

        session.setAttribute("studentId", student.getId());
    }
}
