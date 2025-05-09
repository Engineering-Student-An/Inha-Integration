package an.inhaintegration.icross.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id @Column(name = "task_id")
    private Long id;

    // 할 일 이름
    private String name;

    // 출석 인정 기간
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnivInfoTask> univInfoTaskList = new ArrayList<>();

    public void setSubject(Subject subject) {
        this.subject = subject;
        subject.getTaskList().add(this);
    }
}
