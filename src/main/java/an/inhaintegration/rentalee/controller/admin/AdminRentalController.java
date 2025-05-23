package an.inhaintegration.rentalee.controller.admin;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.rental.RentalSearchRequestDto;
import an.inhaintegration.rentalee.service.RentalService;
import an.inhaintegration.rentalee.service.admin.AdminRentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminRentalController {

    private final RentalService rentalService;
    private final AdminRentalService adminRentalService;

    @GetMapping("/rentals")
    public String allList(@ModelAttribute("rentalSearchRequestDto") RentalSearchRequestDto rentalSearchRequestDto,
                          @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                          Model model){

        model.addAttribute("rentals", adminRentalService.findRentalsBySearch(rentalSearchRequestDto, page));

        return "rentalee/admin/rental/list";
    }

    @DeleteMapping("/rental/{rentalId}")
    public String adminFinishRental(@PathVariable("rentalId") Long rentalId,
                                    @RequestParam("studentId") Long studentId, RedirectAttributes redirectAttributes) {

        rentalService.finishRental(rentalId);

        redirectAttributes.addAttribute("studentId", studentId);
        return "redirect:/admin/student/{studentId}";
    }


    @ModelAttribute("loginStudent")
    public Student loginStudent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getStudent();
        }

        return null;
    }
}

