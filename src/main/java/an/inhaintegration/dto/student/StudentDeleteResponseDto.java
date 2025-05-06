package an.inhaintegration.dto.student;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StudentDeleteResponseDto {

    private boolean success;
    private String message;
    private String nextUrl;
}
