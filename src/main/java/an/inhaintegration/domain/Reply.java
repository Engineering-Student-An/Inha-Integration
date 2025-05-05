package an.inhaintegration.domain;

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
    private Board board;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @Setter
    private Student student;

    @ElementCollection
    private List<String> likeNumber = new ArrayList<>();

    @Column(length = 5000, nullable = false)
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public void setBoard(Board board){
        this.board = board;
        board.getReplyList().add(this);
    }

    // 좋아요 메서드
    public void like(String stuId){
        if(this.likeNumber.contains(stuId)){
            this.likeNumber.remove(stuId);
        } else {
            this.likeNumber.add(stuId);
        }
    }

    // 좋아요 검증 메서드
    public boolean isLike(String stuId) {
        return this.likeNumber.contains(stuId);
    }
}
