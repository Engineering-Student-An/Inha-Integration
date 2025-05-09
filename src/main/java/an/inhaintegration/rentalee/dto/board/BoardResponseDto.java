package an.inhaintegration.rentalee.dto.board;

import an.inhaintegration.rentalee.dto.student.StudentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
public class BoardResponseDto {

    private Long boardId;
    private boolean notice;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private StudentResponseDto student;
    private List<Long> likeNumber;

    // 좋아요 검증 메서드
    public boolean isLike(Long studentId) {
        return this.likeNumber.contains(studentId);
    }
}
