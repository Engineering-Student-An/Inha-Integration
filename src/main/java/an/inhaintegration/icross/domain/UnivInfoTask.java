package an.inhaintegration.icross.domain;

import an.inhaintegration.icross.dto.TaskResponseDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UnivInfoTask { // Task, UnivInfo 사이의 중간 테이블

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="univ_info_task_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "univ_info_id")
    private UnivInfo univInfo;

    @ManyToOne
    @JoinColumn(name = "task_id")
    private Task task;

    private boolean completed; // 과제 완료 여부

    // 완료 메서드
    public void setCompleted() {
        this.completed = true;
    }

    public void setUnivInfoAndTask(UnivInfo univInfo, Task task) {
        this.univInfo = univInfo;
        univInfo.getUnivInfoTaskList().add(this);

        this.task = task;
        task.getUnivInfoTaskList().add(this);
    }

    public TaskResponseDto mapUnivInfoToTaskResponseDto() {

        return new TaskResponseDto(this.task.getId(), this.task.getSubject().getName(), this.task.getName(), this.task.getDeadline());
    }
}
