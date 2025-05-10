package an.inhaintegration.icross.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubjectResponseDto {

    public SubjectResponseDto(String name, String time) {
        this.name = name;
        this.time = time;
    }

    private String name;
    private String code;
    private String time;
}