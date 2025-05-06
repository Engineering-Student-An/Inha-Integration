package an.inhaintegration.service;

import an.inhaintegration.domain.Student;
import an.inhaintegration.dto.ChangePasswordRequestDto;
import an.inhaintegration.dto.OauthUserRequestDto;
import an.inhaintegration.dto.UserRequestDto;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.BoardRepository;
import an.inhaintegration.repository.RentalRepository;
import an.inhaintegration.repository.ReplyRepository;
import an.inhaintegration.repository.StudentRepository;
import jakarta.servlet.http.HttpServletRequest;
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
    public void updateOauthInfo(Long studentId, OauthUserRequestDto oauthUserRequestDto, HttpServletRequest request) {

        // 로그인한 회원 조회
        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        // 추가적인 정보 업데이트
        loginStudent.addInfoAfterOauth(oauthUserRequestDto.getStuId(), oauthUserRequestDto.getName(), oauthUserRequestDto.getPhoneNumber());

        // 영속성 컨텍스트에 loginStudent가 관리되고 있지 않으므로 save() 메서드를 호출해줘야함 (더티체킹이 일어나지 않기 때문)
        studentRepository.save(loginStudent);
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
            bindingResult.addError(new FieldError("request",
                    "changePassword", "비밀번호가 동일하지 않습니다!"));
            bindingResult.addError(new FieldError("request",
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

        // 비밀번호 검증 완료 시 null 반환
        return null;
    }

    @Transactional
    public void editEmail(Long studentId, String email) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        loginStudent.editEmail(email);
    }

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
