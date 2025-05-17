package an.inhaintegration.monitoring;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/monitoring")
public class MonitoringApiController {

    @GetMapping("/slow")
    public String slowEndpoint() throws InterruptedException {
        Thread.sleep(5000); // 5초 지연
        return "Delayed response";
    }
}
