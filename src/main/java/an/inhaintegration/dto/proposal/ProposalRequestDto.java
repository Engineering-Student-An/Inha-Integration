package an.inhaintegration.dto.proposal;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProposalRequestDto {

    private String stuId;
    private String name;
    private String title;
    private String content;
    private boolean secret;
}
