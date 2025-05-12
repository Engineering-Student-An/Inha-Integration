package an.inhaintegration.rentalee.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentInfoRequestDto {

    private String loginId;
    private String name;
    private String phoneNumber;
}
