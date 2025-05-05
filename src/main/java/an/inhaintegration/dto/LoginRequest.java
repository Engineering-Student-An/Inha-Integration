package an.inhaintegration.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class LoginRequest {

    private String stuId;
    private String password;
}
