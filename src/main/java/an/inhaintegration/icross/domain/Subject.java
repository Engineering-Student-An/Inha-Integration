package an.inhaintegration.icross.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subject {

    @Id @Column(name = "subject_id")
    private Long id;

    // 학수번호
    private String code;

    // 과목명
    private String name;

    @OneToMany(mappedBy = "subject", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> taskList = new ArrayList<>();

    public List<Task> getTaskList() {
        if (taskList == null) {
            taskList = new ArrayList<>();  // null이 아니라 기본값을 리턴
        }
        return taskList;
    }

    // 수업 시간
    @ElementCollection
    private List<String> timeList = new ArrayList<>();
}
