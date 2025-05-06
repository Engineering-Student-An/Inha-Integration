package an.inhaintegration.dto.board;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BoardResponseDto {

    private Long boardId;
    private String title;
    private LocalDateTime createdAt;
}
