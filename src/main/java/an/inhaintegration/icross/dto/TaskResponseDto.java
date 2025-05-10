package an.inhaintegration.icross.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponseDto {

    private Long webId;
    private String subjectName;
    private String taskName;
    private LocalDateTime deadLine;
}
