package an.inhaintegration.service;

import an.inhaintegration.domain.FeeStudent;
import an.inhaintegration.dto.OauthUserRequestDto;
import an.inhaintegration.dto.UserRequestDto;
import an.inhaintegration.repository.FeeStudentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class FeeStudentService {

    private final FeeStudentRepository feeStudentRepository;
//
//    // 학생회비 납부 명단 학번, 이름 포함 조회
//    public Page<FeeStudent> findFeeStudentContainingStuIdAndName(String stuId, String name, Pageable pageable) {
//        return feeStudentRepository.findFeeStudentByStuIdContainingAndNameContaining(stuId, name, pageable);
//    }
//
//    // 학생회비 납부 명단 전체 조회
//    public Page<FeeStudent> findAllFeeStudent(Pageable pageable) {
//        return feeStudentRepository.findAll(pageable);
//    }
//
//    public boolean checkFeeStudentDuplicate(String stuID) {
//        return feeStudentRepository.existsByStuId(stuID);
//    }
//
//
//    @Transactional
//    public void save(FeeStudent feeStudent) {
//        feeStudentRepository.save(feeStudent);
//    }
//

    // 학번으로 조회 메서드
    public FeeStudent findByStuId(String stuId) {

        return feeStudentRepository.findByStuId(stuId).orElse(null);
    }

    // 학생회비 납부 명단에서 검증하는 메서드
    public void validateFeeStudent(@Valid UserRequestDto userRequestDto, BindingResult bindingResult) {

        FeeStudent feeStudent = findByStuId(userRequestDto.getStuId());

        if(feeStudent == null) {
            bindingResult.addError(new FieldError("userRequestDto",
                    "stuId", "학생회비 납부 명단에 존재하지 않는 학번입니다!"));
        } else if(!feeStudent.getName().equals(userRequestDto.getName())) {
            bindingResult.addError(new FieldError("userRequestDto",
                    "name", "학번과 일치하지 않는 이름입니다!"));
        }
    }

    // oauth 로그인 시 학생회비 납부 명단에서 검증하는 메서드
    public void validateOauthFeeStudent(@Valid OauthUserRequestDto oauthUserRequestDto, BindingResult bindingResult) {

        if (!Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$").matcher(oauthUserRequestDto.getPhoneNumber()).matches()) {
            bindingResult.addError(new FieldError("oauthUserRequestDto",
                    "phoneNumber", "전화번호 형식이 올바르지 않습니다!"));
        }

        FeeStudent feeStudent = findByStuId(oauthUserRequestDto.getStuId());

        if(feeStudent == null) {
            bindingResult.addError(new FieldError("oauthUserRequestDto",
                    "stuId", "학생회비 납부 명단에 존재하지 않는 학번입니다!"));
        } else if(!feeStudent.getName().equals(oauthUserRequestDto.getName())) {
            bindingResult.addError(new FieldError("oauthUserRequestDto",
                    "name", "학번과 일치하지 않는 이름입니다!"));
        }
    }
//
//    @Transactional
//    public void saveAll(List<FeeStudent> feeStudents) {
//        feeStudentRepository.saveAll(feeStudents);
//    }
//
//    @Transactional
//    public void delete(String stuId) {
//        feeStudentRepository.delete(feeStudentRepository.findByStuId(stuId));
//    }
//
//    @Transactional
//    public void deleteAll() {
//        feeStudentRepository.deleteAll();
//    }

}
