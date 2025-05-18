package an.inhaintegration.monitoring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/monitoring")
public class MonitoringApiController {

    @GetMapping("/slow")
    public String slowEndpoint() throws InterruptedException {
        Thread.sleep(5000); // 5초 지연
        return "Delayed response";
    }

    private List<String> memoryHog = new ArrayList<>();

    @GetMapping("/memory-leak")
    public String memoryLeak() {
        for (int i = 0; i < 100000; i++) {
            memoryHog.add(UUID.randomUUID().toString());
        }
        return "Memory inflated";
    }
}
