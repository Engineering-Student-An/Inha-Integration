package an.inhaintegration.rentalee.dto.student;

import an.inhaintegration.rentalee.domain.StudentRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentOauthRequestDto {

    @NotBlank(message = "학번이 비어있습니다.")
    private String stuId;

    @NotBlank(message = "이름이 비어있습니다.")
    private String name;

    @NotBlank(message = "전화번호가 비어있습니다.")
    private String phoneNumber;

    private StudentRole role = StudentRole.USER;
}
