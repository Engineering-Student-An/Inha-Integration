package an.inhaintegration.domain;

import an.inhaintegration.dto.proposal.ProposalResponseDto;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners({AuditingEntityListener.class})
public class Proposal {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "proposal_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    @Setter
    private Student student;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 5000)
    private String content;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // 비밀글 여부
    private boolean isSecret;

    public ProposalResponseDto toProposalResponseDto() {

        return new ProposalResponseDto(this.id, this.title, this.content, this.createdAt, this.student.toStudentResponseDto());
    }
}
