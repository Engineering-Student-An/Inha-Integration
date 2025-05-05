package an.inhaintegration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ParserDto {
    private String title;
    private String link;
    private String date;
}
