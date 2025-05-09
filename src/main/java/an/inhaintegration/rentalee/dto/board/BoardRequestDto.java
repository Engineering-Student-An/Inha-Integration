package an.inhaintegration.rentalee.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardRequestDto {

    private String title;
    private String content;

    private boolean notice;
}
