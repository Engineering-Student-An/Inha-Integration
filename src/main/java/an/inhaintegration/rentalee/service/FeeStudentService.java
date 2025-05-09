package an.inhaintegration.rentalee.service;

import an.inhaintegration.rentalee.domain.FeeStudent;
import an.inhaintegration.rentalee.dto.student.StudentOauthRequestDto;
import an.inhaintegration.rentalee.dto.student.StudentRequestDto;
import an.inhaintegration.rentalee.repository.FeeStudentRepository;
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

    // 학생회비 납부 명단에서 검증하는 메서드
    public void validateFeeStudent(@Valid StudentRequestDto studentRequestDto, BindingResult bindingResult) {

        FeeStudent feeStudent = feeStudentRepository.findByStuId(studentRequestDto.getStuId()).orElse(null);

        if(feeStudent == null) {
            bindingResult.addError(new FieldError("studentRequestDto",
                    "stuId", "학생회비 납부 명단에 존재하지 않는 학번입니다!"));
        } else if(!feeStudent.getName().equals(studentRequestDto.getName())) {
            bindingResult.addError(new FieldError("studentRequestDto",
                    "name", "학번과 일치하지 않는 이름입니다!"));
        }
    }

    // oauth 로그인 시 학생회비 납부 명단에서 검증하는 메서드
    public void validateOauthFeeStudent(@Valid StudentOauthRequestDto studentOauthRequestDto, BindingResult bindingResult) {

        if (!Pattern.compile("^\\d{3}-\\d{3,4}-\\d{4}$").matcher(studentOauthRequestDto.getPhoneNumber()).matches()) {
            bindingResult.addError(new FieldError("studentOauthRequestDto",
                    "phoneNumber", "전화번호 형식이 올바르지 않습니다!"));
        }

        FeeStudent feeStudent = feeStudentRepository.findByStuId(studentOauthRequestDto.getStuId()).orElse(null);

        if(feeStudent == null) {
            bindingResult.addError(new FieldError("studentOauthRequestDto",
                    "stuId", "학생회비 납부 명단에 존재하지 않는 학번입니다!"));
        } else if(!feeStudent.getName().equals(studentOauthRequestDto.getName())) {
            bindingResult.addError(new FieldError("studentOauthRequestDto",
                    "name", "학번과 일치하지 않는 이름입니다!"));
        }
    }
}
