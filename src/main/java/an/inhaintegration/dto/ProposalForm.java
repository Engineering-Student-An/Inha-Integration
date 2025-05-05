package an.inhaintegration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ProposalForm {

    private String stuId;
    private String name;


    private String title;
    private String content;

    private boolean secret;
}
