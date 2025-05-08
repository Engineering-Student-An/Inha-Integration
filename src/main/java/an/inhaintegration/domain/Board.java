package an.inhaintegration.domain;

import an.inhaintegration.dto.board.BoardRequestDto;
import an.inhaintegration.dto.board.BoardResponseDto;
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

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ElementCollection
    private List<Long> likeNumber = new ArrayList<>();

    // 공지사항 여부
    private boolean notice;

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

    public void setStudent(Student student) {
        this.student = student;
        student.getBoardList().add(this);
    }

    public void edit(String title, String content){
        this.title = title;
        this.content = content;
    }

    public BoardResponseDto toBoardResponseDto() {

        return new BoardResponseDto(this.id, this.notice, this.title, this.content,
                this.createdAt, this.updatedAt, this.student.toStudentResponseDto(), this.likeNumber);
    }

    public BoardRequestDto toBoardRequestDto() {

        return new BoardRequestDto(this.title, this.content, this.notice);
    }
}
