package an.inhaintegration.domain;

import an.inhaintegration.dto.BoardForm;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class Board {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<Reply> replyList = new ArrayList<>();

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection
    private List<String> likeNumber = new ArrayList<>();

    // 공지사항 여부
    private boolean notice;

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

    public void edit(BoardForm boardForm){
        this.title = boardForm.getTitle();
        this.content = boardForm.getContent();
    }
}
