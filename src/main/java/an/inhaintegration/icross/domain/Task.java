package an.inhaintegration.icross.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder
@AllArgsConstructor
@NoArgsConstructor
public class Task {

    @Id
    private Long webId;

    // 과목 이름
    private String subjectName;

    // 할 일 이름
    private String name;

    // 출석 인정 기간
    private LocalDateTime deadline;

    @Enumerated(EnumType.STRING)
    private TaskType type;

    @OneToMany(mappedBy = "task", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UnivInfoTask> univInfoTaskList = new ArrayList<>();
}
