package an.inhaintegration.dto.crawling;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CrawlingInhaEEResponseDto {
    private String title;
    private String link;
    private String date;
}
