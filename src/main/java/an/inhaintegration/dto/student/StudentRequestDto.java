package an.inhaintegration.dto.student;

import an.inhaintegration.domain.StudentRole;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentRequestDto {

    @NotBlank(message = "ID가 비어있습니다.")
    private String loginId;

    @NotBlank(message = "학번이 비어있습니다.")
    private String stuId;

    @NotBlank(message = "이름이 비어있습니다.")
    private String name;

    @NotBlank(message = "비밀번호가 비어있습니다.")
    private String password;
    private String passwordCheck;

    @NotBlank(message = "전화번호가 비어있습니다.")
    private String phoneNumber;

    private StudentRole role = StudentRole.USER;
}
