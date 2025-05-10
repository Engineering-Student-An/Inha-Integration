package an.inhaintegration.icross.api;

import an.inhaintegration.icross.service.ScheduleService;
import an.inhaintegration.oauth2.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/i-cross")
public class ApiICrossScheduleController {

    private final ScheduleService scheduleService;

    @PostMapping("/schedule/{scheduleId}")
    public ResponseEntity<Map<String, Object>> completeSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                                @PathVariable("scheduleId") Long scheduleId) {

        try {
            scheduleService.complete(userDetails.getId(), scheduleId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", 400, "message", e.getMessage()));
        }
        return ResponseEntity.ok(Map.of("status", 201));
    }

    @DeleteMapping("/schedule/{scheduleId}")
    public ResponseEntity<Map<String, Object>> deleteSchedule(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                              @PathVariable("scheduleId") Long scheduleId) {

        try {
            scheduleService.delete(userDetails.getId(), scheduleId);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", 400, "message", e.getMessage()));
        }
        return ResponseEntity.ok(Map.of("status", 204));
    }
}
