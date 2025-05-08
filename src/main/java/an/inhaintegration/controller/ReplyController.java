package an.inhaintegration.controller;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import an.inhaintegration.dto.reply.ReplyRequestDto;
import an.inhaintegration.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class ReplyController {

    private final ReplyService replyService;

    @PostMapping("/board/{boardId}/reply")
    public String createReply(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PathVariable("boardId") Long boardId,
                              @ModelAttribute("replyRequestDto") ReplyRequestDto replyRequestDto, BindingResult bindingResult) {

        // 댓글 유효성 검증
        replyService.validateReply(replyRequestDto, bindingResult);

        if(bindingResult.hasErrors()) return "board/showOne";

        replyService.save(userDetails.getId(), boardId, replyRequestDto);

        return "redirect:/board/" + boardId;
    }

    @DeleteMapping("/board/{boardId}/reply/{replyId}")
    public String deleteReply(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PathVariable("boardId") Long boardId,
                              @PathVariable("replyId") Long replyId) {

        replyService.delete(userDetails.getId(), replyId);

        return "redirect:/board/" + boardId;
    }

    @PostMapping("/board/{boardId}/reply/{replyId}/like")
    public String likeReply(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @PathVariable("boardId") Long boardId,
                            @PathVariable("replyId") Long replyId) {

        replyService.like(userDetails.getId(), replyId);

        return "redirect:/board/" + boardId;
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