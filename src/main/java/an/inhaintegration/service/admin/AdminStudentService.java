package an.inhaintegration.service.admin;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.StudentRole;
import an.inhaintegration.dto.student.StudentDeleteResponseDto;
import an.inhaintegration.dto.student.StudentResponseDto;
import an.inhaintegration.dto.student.StudentSearchRequestDto;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.StudentRepository;
import an.inhaintegration.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminStudentService {

    private final StudentRepository studentRepository;
    private final StudentService studentService;

    public Page<Student> findStudentsBySearch(int page, StudentSearchRequestDto studentSearchRequestDto) {

        // 학번, 이름 모두 포함하는 학생 조회
        if (studentSearchRequestDto.getStuId() != null && studentSearchRequestDto.getName() != null) {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId").descending());
            return studentRepository.findStudentsByStuIdContainingAndNameContaining(studentSearchRequestDto.getStuId(), studentSearchRequestDto.getName(), pageRequest);
        } else {    // 전체 학생 조회
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId").descending());
            return studentRepository.findAll(pageRequest);
        }
    }

    public StudentResponseDto findById(Long studentId) {

        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        return mapStudentToStudentResponseDto(student);
    }

    public StudentResponseDto mapStudentToStudentResponseDto(Student student) {

        return new StudentResponseDto(student.getId(), student.getPicture(), student.getStuId(), student.getName(), student.getPhoneNumber(), student.getEmail());
    }

    @Transactional
    public StudentDeleteResponseDto delete(Long studentId, Long loginStudentId, String password) {

        if (!studentService.passwordCheck(loginStudentId, password)) {
            return new StudentDeleteResponseDto(false, "비밀번호가 일치하지 않습니다!", "/admin/student/" + studentId);
        }

        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        if(student.getRole().equals(StudentRole.ADMIN) ) {
            return new StudentDeleteResponseDto(false, "관리자 계정은 삭제할 수 없습니다.", "/admin/student/" + studentId);
        }

        try {
            studentRepository.delete(student);
            return new StudentDeleteResponseDto(true, "계정 삭제가 완료되었습니다.\n대여 정보, 게시글, 댓글이 모두 삭제되었습니다.", "/admin/students");
        } catch (Exception ex) {
            return new StudentDeleteResponseDto(false, "대여중인 물품을 모두 반납하고 계정 삭제를 진행해주세요!", "/admin/student/" + studentId);
        }
    }
}
