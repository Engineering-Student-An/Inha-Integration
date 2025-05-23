package an.inhaintegration.rentalee.dto.proposal;

import an.inhaintegration.rentalee.dto.student.StudentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProposalResponseDto {

    private Long proposalId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private StudentResponseDto student;
}
