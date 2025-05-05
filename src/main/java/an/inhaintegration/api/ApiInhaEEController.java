package an.inhaintegration.api;

import an.inhaintegration.dto.ParserDto;
import an.inhaintegration.service.CrawlingInhaEEService;
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
    public List<ParserDto> fetchImportantPosts() {
        return crawlingInhaEEService.importantPostParser();
    }

}
