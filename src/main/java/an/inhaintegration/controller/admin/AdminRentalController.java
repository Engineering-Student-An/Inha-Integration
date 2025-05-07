package an.inhaintegration.controller.admin;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import an.inhaintegration.dto.rental.RentalSearchRequestDto;
import an.inhaintegration.service.RentalService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/rentals")
    public String allList(@ModelAttribute("rentalSearch") RentalSearchRequestDto rentalSearchRequestDto,
                          @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                          Model model){

        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("rentalDate").descending().and(Sort.by("status").descending()));


//        Page<Rental> rentalByStuIdStatusItem = rentalService.findRentalByStuId_Status_Item(rentalSearchRequestDto, pageRequest);
//        model.addAttribute("rentals", rentalByStuIdStatusItem);

        return "admin/rental/list";
    }

    @PostMapping("/rental/{rentalId}/finish")
    public String adminFinishRental(@PathVariable("rentalId") Long rentalId, @RequestParam String stuId, RedirectAttributes redirectAttributes) {

        rentalService.finishRental(rentalId);

        redirectAttributes.addAttribute("stuId", stuId);
        return "redirect:/admin/student/{stuId}";
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

