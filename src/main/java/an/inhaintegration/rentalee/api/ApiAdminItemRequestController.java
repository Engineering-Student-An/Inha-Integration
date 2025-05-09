package an.inhaintegration.rentalee.api;

import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.service.admin.AdminItemRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")
public class ApiAdminItemRequestController {

    private final AdminItemRequestService adminItemRequestService;

    @PostMapping("/item/request/{itemRequestId}")
    public ResponseEntity<Void> checkRequest(@AuthenticationPrincipal CustomUserDetails userDetails,
                                             @PathVariable("itemRequestId") Long itemRequestId) {

        adminItemRequestService.check(userDetails.getId(), itemRequestId);

        return ResponseEntity.ok().build();
    }
}
