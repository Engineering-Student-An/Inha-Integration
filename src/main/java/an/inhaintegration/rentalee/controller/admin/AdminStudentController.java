package an.inhaintegration.rentalee.controller.admin;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.rental.RentalSearchRequestDto;
import an.inhaintegration.rentalee.dto.student.StudentDeleteResponseDto;
import an.inhaintegration.rentalee.dto.student.StudentSearchRequestDto;
import an.inhaintegration.rentalee.service.admin.AdminRentalService;
import an.inhaintegration.rentalee.service.admin.AdminStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminStudentController {

    private final AdminStudentService adminStudentService;
    private final AdminRentalService adminRentalService;

    @GetMapping("/students")
    public String list(@ModelAttribute("studentSearchRequestDto") StudentSearchRequestDto studentSearchRequestDto,
                       @RequestParam(required = false, defaultValue = "1", value = "page") int page, Model model) {

        model.addAttribute("students", adminStudentService.findStudentsBySearch(page, studentSearchRequestDto));

        return"rentalee/admin/student/list";
    }

    @GetMapping("/student/{studentId}")
    public String findOneStudent(@PathVariable("studentId") Long studentId, Model model,
                                 @ModelAttribute("rentalSearchRequestDto") RentalSearchRequestDto rentalSearchRequestDto,
                                 @RequestParam(required = false, defaultValue = "1", value = "page") int page) {

        model.addAttribute("student", adminStudentService.findById(studentId));
        model.addAttribute("rentalList", adminRentalService.findRentalsBySearch(studentId, rentalSearchRequestDto, page));

        return "rentalee/admin/student/studentInfo";
    }

    @DeleteMapping("/student/{studentId}")
    public String deleteStudent(@AuthenticationPrincipal CustomUserDetails userDetails,
                                @PathVariable("studentId") Long studentId,
                                @RequestParam("password") String password, Model model) {

        StudentDeleteResponseDto studentDeleteResponseDto = adminStudentService.delete(studentId, userDetails.getId(), password);

        model.addAttribute("errorMessage", studentDeleteResponseDto.getMessage());
        model.addAttribute("nextUrl", studentDeleteResponseDto.getNextUrl());

        return "error/errorMessage";
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
