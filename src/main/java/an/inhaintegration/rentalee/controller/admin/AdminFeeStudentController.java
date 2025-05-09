package an.inhaintegration.rentalee.controller.admin;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.student.StudentSearchRequestDto;
import an.inhaintegration.rentalee.service.ExcelService;
import an.inhaintegration.rentalee.service.admin.AdminStudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminFeeStudentController {

    private final AdminStudentService adminStudentService;
    private final ExcelService excelService;

    @GetMapping("/fee-students")
    public String feeList(@ModelAttribute("studentSearchRequestDto") StudentSearchRequestDto studentSearchRequestDto,
                          @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                          Model model) {

        model.addAttribute("students", adminStudentService.findFeeStudentsBySearch(studentSearchRequestDto, page));

        return"rentalee/admin/student/feeList";
    }

    @PostMapping("/fee-student")
    public String createFeeStudentForm(@RequestParam("newStuId") String newStuId,
                                       @RequestParam("newName") String newName, Model model) {

        String message = adminStudentService.createFeeStudent(newStuId, newName);

        model.addAttribute("errorMessage", message);
        model.addAttribute("nextUrl", "/admin/fee-students");

        return "error/errorMessage";
    }

    @DeleteMapping("/fee-students")
    public String deleteFeeStudents(@RequestParam("stuIdList") List<String> stuIdList) {

        adminStudentService.deleteFeeStudents(stuIdList);

        return "redirect:/admin/fee-students";
    }

    @PostMapping("/fee-students")
    public String uploadExcelFile(@RequestParam("excelFile") MultipartFile file,
                                  Model model) throws IOException {

        // 파일 유효성 검증
        String errorMessage = excelService.validateExcelFile(file);
        if(errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            model.addAttribute("nextUrl", "/admin/fee-students");

            return "error/errorMessage";
        }

        adminStudentService.createFeeStudentsByExcelFile(excelService.uploadExcel(file));

        return "redirect:/admin/fee-students";
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
