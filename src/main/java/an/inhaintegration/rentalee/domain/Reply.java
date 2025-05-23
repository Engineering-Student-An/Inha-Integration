package an.inhaintegration.rentalee.domain;

import an.inhaintegration.rentalee.dto.reply.ReplyResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class Reply {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    @Setter
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @Setter
    private Student student;

    @ElementCollection
    private List<Long> likeNumber = new ArrayList<>();

    @Column(length = 5000, nullable = false)
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 좋아요 메서드
    public void like(Long studentId){
        if(this.likeNumber.contains(studentId)){
            this.likeNumber.remove(studentId);
        } else {
            this.likeNumber.add(studentId);
        }
    }

    // 좋아요 검증 메서드
    public boolean isLike(Long studentId) {
        return this.likeNumber.contains(studentId);
    }

    public ReplyResponseDto toReplyResponseDto() {

        return new ReplyResponseDto(this.id, this.content, this.student.toStudentResponseDto(), this.createdAt, this.likeNumber);
    }
}
