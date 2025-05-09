package an.inhaintegration.rentalee.dto.reply;

import an.inhaintegration.rentalee.dto.student.StudentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReplyResponseDto {

    private Long replyId;
    private String content;
    private StudentResponseDto student;
    private LocalDateTime createdAt;
    private List<Long> likeNumber;

    // 좋아요 검증 메서드
    public boolean isLike(Long studentId) {
        return this.likeNumber.contains(studentId);
    }
}
