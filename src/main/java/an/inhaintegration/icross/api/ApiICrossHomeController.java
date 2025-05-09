package an.inhaintegration.icross.api;

import an.inhaintegration.icross.service.CoursemosService;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.service.UnivInfoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ApiICrossHomeController {

    private final CoursemosService coursemosService;
    private final UnivInfoService univInfoService;

    @GetMapping("/i-cross/login")
    public ResponseEntity<Map<String, Object>> loginCoursemose(@AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
              coursemosService.fromLoginToSaveUnivInfoTask(userDetails.getStudent().getStuId(), userDetails.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", 400, "message", "로그인 실패"));
        }
        return ResponseEntity.ok(Map.of("status", 200, "message", "성공"));
    }

    @GetMapping("/i-cross/tasks")
    public ResponseEntity<Map<String, Object>> createTasks(@AuthenticationPrincipal CustomUserDetails userDetails) {

        try {
            univInfoService.saveUnivInfoTask(userDetails.getId());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("status", 400, "message", "로그인 실패"));
        }
        return ResponseEntity.ok(Map.of("status", 200, "message", "성공"));
    }
}
