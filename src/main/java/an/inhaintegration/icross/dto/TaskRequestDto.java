package an.inhaintegration.icross.dto;

import an.inhaintegration.icross.domain.TaskType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {

    private Long webId; // 강의 id
    private Long subjectId; // 과목 이름
    private String name;    // 할 일 이름
    private TaskType taskType;
}
