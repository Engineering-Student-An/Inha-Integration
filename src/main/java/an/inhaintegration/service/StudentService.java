package an.inhaintegration.service;

import an.inhaintegration.domain.Student;
import an.inhaintegration.dto.OauthUserRequestDto;
import an.inhaintegration.dto.UserRequestDto;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.BoardRepository;
import an.inhaintegration.repository.RentalRepository;
import an.inhaintegration.repository.ReplyRepository;
import an.inhaintegration.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final RentalRepository rentalRepository;
    private final BoardRepository boardRepository;
    private final ReplyRepository replyRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    // 학번 중복 검증 메서드
    public boolean checkStuIdDuplicate(String stuId){
        return studentRepository.existsByStuId(stuId);
    }

    // 회원가입 검증 메서드
    public void validateJoin(@Valid UserRequestDto userRequestDto, BindingResult bindingResult) {

        if (checkStuIdDuplicate(userRequestDto.getStuId())) {
            bindingResult.addError(new FieldError("userRequestDto",
                    "stuId", "이미 가입되어 있습니다!"));
        }

        if (!userRequestDto.getPassword().equals(userRequestDto.getPasswordCheck())) {
            bindingResult.addError(new FieldError("userRequestDto",
                    "passwordCheck", "비밀번호가 동일하지 않습니다!"));
        }

        if (!Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$").matcher(userRequestDto.getPhoneNumber()).matches()) {
            bindingResult.addError(new FieldError("userRequestDto",
                    "phoneNumber", "전화번호 형식이 올바르지 않습니다!"));
        }
    }

    // 회원가입
    @Transactional
    public void join(UserRequestDto userRequestDto, String email){

        String encodedPassword = bCryptPasswordEncoder.encode(userRequestDto.getPassword());

        Student newStudent = Student.builder()
                .loginId(userRequestDto.getLoginId())
                .stuId(userRequestDto.getStuId())
                .name(userRequestDto.getName())
                .password(encodedPassword)
                .phoneNumber(userRequestDto.getPhoneNumber())
                .role(userRequestDto.getRole())
                .email(email)
                .build();

        studentRepository.save(newStudent);
    }

    @Transactional
    public void updateOauthInfo(OauthUserRequestDto oauthUserRequestDto, HttpServletRequest request) {

        // 세션에서 로그인한 사용자 가져오기
        HttpSession session = request.getSession();
        Student loginStudent = (Student) session.getAttribute("loginStudent");

        if (loginStudent == null) throw new StudentNotFoundException();

        // DB에서 해당 학생 엔티티 조회
        Student student = studentRepository.findById(loginStudent.getId())
                .orElseThrow((StudentNotFoundException::new));

        // stuId 업데이트
        student.addInfoAfterOauth(oauthUserRequestDto.getStuId(), oauthUserRequestDto.getName(), oauthUserRequestDto.getPhoneNumber());

        // 세션 정보도 업데이트 (선택적)
        session.setAttribute("loginStudent", student);
    }
//
//    public boolean passwordCheck(String stuId, String password) {
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//
//        return encoder.matches(password, studentRepository.findByStuId(stuId).getPassword());
//    }
//
//    // 비밀번호 변경
//    public void changePassword(String stuId, ChangePasswordRequest request) {
//        Student student = studentRepository.findByStuId(stuId);
//        student.editPassword(bCryptPasswordEncoder.encode(request.getChangePassword()));
//    }
//
//    public void changeEmail(String stuId, String email) {
//        Student student = studentRepository.findByStuId(stuId);
//        student.editEmail(email);
//    }
//
//    private final BoardService boardService;
//    private final ReplyService replyService;
//
//    // 회원 탈퇴
//    @Transactional
//    public boolean delete(String stuId) {
//        Student findStudent = studentRepository.findByStuId(stuId);
//
//        Collection<RentalStatus> statuses = new ArrayList<>();
//        statuses.add(RentalStatus.FINISH);
//        statuses.add(RentalStatus.FINISH_OVERDUE);
//
//        if(!rentalRepository.existsByStudent_IdAndStatusNotIn(findStudent.getId(), statuses)) {
//
//            // 대여 기록 삭제
//            for (Rental rental : rentalRepository.findRentalsByStudent_IdAndStatusIn(findStudent.getId(), statuses)) {
//                rentalRepository.delete(rental);
//            }
//
//            // 학생 정보 삭제
//            studentRepository.delete(findStudent);
//
//            // 게시글의 학생 정보 수정
//            for (Board board : boardService.findBoardsByStuId(stuId)) {
//                boardRepository.delete(board);
//            }
//
//            // 댓글의 학생 정보 수정
//            for (Reply reply : replyService.findRepliesByStuId(stuId)) {
//                replyRepository.delete(reply);
//            }
//
//            // 게시글의 좋아요 리스트에서 삭제
//            for (Board board : boardRepository.findAll()) {
//                if(board.isLike(stuId)) {
//                    board.like(stuId);
//                }
//            }
//
//            // 댓글의 좋아요 리스트에서 삭제
//            for(Reply reply : replyRepository.findAll()) {
//                if(reply.isLike(stuId)) {
//                    reply.like(stuId);
//                }
//            }
//            return true;
//        } else {
//            return false;
//        }
//
//    }
//
//    // 로그인
//    public Student login(LoginRequest loginRequest) {
//        Student student = studentRepository.findByStuId(loginRequest.getStuId());
//
//        if (student == null) {
//            return null;
//        }
//
//        if(!student.getPassword().equals(loginRequest.getPassword())){
//            return null;
//        }
//
//        return student;
//    }
//
//    /**
//     * 학생 단건 조회
//     */
//    public Student findStudent(String stuId){
//        return studentRepository.findByStuId(stuId);
//
//    }
//
//    public Page<Student> findStudentContainingStuIdAndName(String stuId, String name, Pageable pageable){
//        return studentRepository.findStudentByStuIdContainingAndNameContaining(stuId, name, pageable);
//    }
//
//    /**
//     * 학생 전체 조회
//     */
//    public Page<Student> findAllStudent(Pageable pageable){
//        return studentRepository.findAll(pageable);
//    }
}
