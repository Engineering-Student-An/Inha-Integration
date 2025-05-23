package an.inhaintegration.rentalee.dto.student;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class StudentSearchRequestDto {

    private String stuId;
    private String name;
}
