package an.inhaintegration.icross.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleRequestDto {

    private String scheduleName;
    private LocalTime startTime;
    private LocalTime endTime;
}
