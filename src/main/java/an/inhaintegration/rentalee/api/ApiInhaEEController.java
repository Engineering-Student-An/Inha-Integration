package an.inhaintegration.rentalee.api;

import an.inhaintegration.rentalee.dto.crawling.CrawlingInhaEEResponseDto;
import an.inhaintegration.rentalee.service.CrawlingInhaEEService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiInhaEEController {

    private final CrawlingInhaEEService crawlingInhaEEService;

    @GetMapping("/importantPosts")
    public List<CrawlingInhaEEResponseDto> fetchImportantPosts() {
        return crawlingInhaEEService.importantPostParser();
    }

}
