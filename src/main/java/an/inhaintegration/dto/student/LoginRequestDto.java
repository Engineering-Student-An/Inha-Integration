package an.inhaintegration.dto.student;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LoginRequestDto {

    private String loginId;
    private String password;
}
