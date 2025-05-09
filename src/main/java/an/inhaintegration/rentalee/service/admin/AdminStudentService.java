package an.inhaintegration.rentalee.service.admin;

import an.inhaintegration.rentalee.domain.FeeStudent;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.domain.StudentRole;
import an.inhaintegration.rentalee.dto.student.StudentDeleteResponseDto;
import an.inhaintegration.rentalee.dto.student.StudentResponseDto;
import an.inhaintegration.rentalee.dto.student.StudentSearchRequestDto;
import an.inhaintegration.rentalee.exception.StudentNotFoundException;
import an.inhaintegration.rentalee.repository.FeeStudentRepository;
import an.inhaintegration.rentalee.repository.StudentRepository;
import an.inhaintegration.rentalee.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminStudentService {

    private final StudentRepository studentRepository;
    private final StudentService studentService;
    private final FeeStudentRepository feeStudentRepository;

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

        return student.toStudentResponseDto();
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

    // 학생회비 납부 명단 학번, 이름 포함 조회
    public Page<FeeStudent> findFeeStudentsBySearch(StudentSearchRequestDto studentSearchRequestDto, int page) {

        // 학번, 이름 모두 포함하는 학생들 추가
        if (studentSearchRequestDto.getStuId() != null && studentSearchRequestDto.getName() != null) {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId"));
            return feeStudentRepository.findFeeStudentsByStuIdContainingAndNameContaining(studentSearchRequestDto.getStuId(), studentSearchRequestDto.getName(), pageRequest);
        } else {    // 전체 학생 조회
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("stuId"));
            return feeStudentRepository.findAll(pageRequest);
        }
    }

    @Transactional
    public String createFeeStudent(String stuId, String name) {

        // 학번이나 이름이 비어있는 경우
        if (stuId.isEmpty() || name.isEmpty()) return "학번과 이름에 공백이 있는지 확인해주세요!";

        // 동일한 학번이 존재하는 경우
        if (feeStudentRepository.existsByStuId(stuId)) {
            return "동일한 학번이 존재합니다!";
        } else {
            FeeStudent feeStudent = new FeeStudent(stuId, name);
            feeStudentRepository.save(feeStudent);

            return "학생회비 납부 명단에 추가했습니다.";
        }
    }

    // 학번 리스트로 삭제 메서드
    @Transactional
    public void deleteFeeStudents(List<String> stuIdList) {

        feeStudentRepository.deleteByStuIdIn(stuIdList);
    }

    @Transactional
    public void createFeeStudentsByExcelFile(List<FeeStudent> feeStudents) {

        // 학생회비 납부 명단 초기화
        feeStudentRepository.deleteAllInBatch();

        // 학생회비 납부 명단 전체 생성
        feeStudentRepository.saveAll(feeStudents);
    }
}
