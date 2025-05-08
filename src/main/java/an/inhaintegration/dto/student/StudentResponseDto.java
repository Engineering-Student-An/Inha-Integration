package an.inhaintegration.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponseDto {

    private Long studentId;
    private String picture;
    private String stuId;
    private String name;
    private String phoneNumber;
    private String email;
}
