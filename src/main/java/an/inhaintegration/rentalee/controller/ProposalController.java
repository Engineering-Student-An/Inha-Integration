package an.inhaintegration.rentalee.controller;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.proposal.ProposalRequestDto;
import an.inhaintegration.rentalee.service.ProposalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class ProposalController {

    private final ProposalService proposalService;

    @GetMapping("/proposals")
    public String proposalList(@RequestParam(required = false, value = "page", defaultValue = "1") int page,
                               Model model) {

        model.addAttribute("proposals", proposalService.findAll(page));

        return "rentalee/proposal/list";
    }

    @GetMapping("/proposal")
    public String createProposalForm(Model model) {

        model.addAttribute("proposalRequestDto", new ProposalRequestDto());

        return "rentalee/proposal/createProposalForm";
    }

    @PostMapping("/proposal")
    public String createProposal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @ModelAttribute("proposalRequestDto") ProposalRequestDto proposalRequestDto, BindingResult bindingResult) {

        // 건의 사항 유효성 검증
        proposalService.validateProposal(proposalRequestDto, bindingResult);

        if (bindingResult.hasErrors()) return "rentalee/proposal/createProposalForm";

        proposalService.save(userDetails.getId(), proposalRequestDto);

        return "redirect:/proposals";
    }

    @GetMapping("/proposal/{proposalId}")
    public String showOneProposal(@AuthenticationPrincipal CustomUserDetails userDetails,
                                  @PathVariable("proposalId") Long proposalId, Model model) {

        // 접근 권한 체크
        proposalService.checkAccess(userDetails.getId(), proposalId);

        model.addAttribute("proposal", proposalService.findById(proposalId));

        return "rentalee/proposal/showOne";
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
