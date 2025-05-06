package an.inhaintegration.service;

import an.inhaintegration.domain.Student;
import an.inhaintegration.exception.StudentNotFoundException;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoginStudentUtil {

    private final HttpSession session;

    // 세션에서 로그인한 회원 정보 조회 메서드
    public Optional<Student> getLoginStudent() {

        Student loginStudent = (Student) session.getAttribute("loginStudent");
        return Optional.ofNullable(loginStudent);
    }

    // Oauth 로그인인지 확인하는 메서드
    public boolean isOauthLogin() {

        Student loginStudent = getLoginStudent().orElseThrow(StudentNotFoundException::new);

        return loginStudent.getProvider() != null;
    }
}
