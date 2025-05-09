package an.inhaintegration.rentalee.controller;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.rental.RentalSearchRequestDto;
import an.inhaintegration.rentalee.exception.RentalOverdueException;
import an.inhaintegration.rentalee.service.ItemService;
import an.inhaintegration.rentalee.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class RentalController {

    private final RentalService rentalService;
    private final ItemService itemService;

    @GetMapping("/rental")
    public String createForm(Model model) {

        model.addAttribute("isMobile", model.getAttribute("isMobile"));

        return "rentalee/rental/rentalForm";
    }

    @GetMapping("/rental/{category}")
    public String rental(@PathVariable("category") String category, Model model) {

        model.addAttribute("selectedCategory", category);

        // 카테고리에 속한 아이템 추가
        model.addAttribute("itemsByCategory", itemService.findItemsByCategory(category));

        model.addAttribute("isMobile", model.getAttribute("isMobile"));

        return "rentalee/rental/rentalForm";
    }

    @PostMapping("/rental/{itemId}")
    @ResponseBody
    public ResponseEntity<String> rentalComplete(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                 @PathVariable("itemId") Long itemId) {

        // 이미 대여중인 물품인 경우
        if (rentalService.existsByStudentIdAndItemId(userDetails.getId(), itemId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

        rentalService.save(userDetails.getId(), itemId);

        return ResponseEntity.status(HttpStatus.OK).body("/home");
    }

    @GetMapping("/rentals")
    public String rentalList(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                             @ModelAttribute("rentalSearchRequestDto") RentalSearchRequestDto rentalSearchRequestDto,
                             Model model) {

        model.addAttribute("myRentalList", rentalService.findRentalsBySearch(userDetails.getId(), rentalSearchRequestDto, page));

        return "rentalee/rental/my-rentals";
    }

    @DeleteMapping("/rental/{rentalId}")
    public String finishRental(@PathVariable("rentalId") Long rentalId, Model model,
                               @RequestHeader(value = "Referer", required = false) String referer) {

        try {
            rentalService.finishRental(rentalId);
            // 현재 페이지로 리디렉션
            return "redirect:" + (referer != null ? referer : "/rentals");
        } catch (RentalOverdueException ex) {
            model.addAttribute("errorMessage", "연체중인 물품의 반납은 관리자를 통해 가능합니다.");
            model.addAttribute("nextUrl", referer);
            model.addAttribute("newUrl", "https://pf.kakao.com/_CxjxlxiM");
            return "error/errorMessageNewTab";
        }
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getStudent();
        }

        return null;
    }

    @ModelAttribute("categories")
    public List<String> allCategories() {

        return itemService.findDistinctCategories();
    }
}
