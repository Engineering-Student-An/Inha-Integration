package an.inhaintegration.icross.api;

import an.inhaintegration.icross.service.TaskService;
import an.inhaintegration.oauth2.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/i-cross")
public class ApiICrossTaskController {

    private final TaskService taskService;

    @PostMapping("/task/{webId}")
    public ResponseEntity<Map<String, Object>> completeTask(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                            @PathVariable("webId") Long webId) {

        try {
            taskService.completeTask(userDetails.getId(), webId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", 400, "message", e.getMessage()));
        }
        return ResponseEntity.ok(Map.of("status", 200));
    }
}
