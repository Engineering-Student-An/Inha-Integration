package an.inhaintegration.rentalee.controller;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.board.BoardRequestDto;
import an.inhaintegration.rentalee.dto.reply.ReplyRequestDto;
import an.inhaintegration.rentalee.service.BoardService;
import an.inhaintegration.rentalee.service.ReplyService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;
    private final ReplyService replyService;

    @GetMapping("/boards")
    public String boardList(@RequestParam(required = false, value = "page", defaultValue = "1") int page,
                            @RequestParam(required = true, value = "notice") boolean notice,
                            Model model) {


        model.addAttribute("boards", boardService.findBoardsByNotice(page, notice));

        // 자유게시판에서는 최근 공지사항 추가
        if(!notice) model.addAttribute("recentNotices", boardService.findRecentNotice());

        return (notice) ? "rentalee/board/notice" : "rentalee/board/list";
    }

    @GetMapping("/board")
    public String createBoardForm(Model model) {

        model.addAttribute("boardRequestDto", new BoardRequestDto());

        return "rentalee/board/createBoardForm";
    }

    @PostMapping("/board")
    public String createBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @ModelAttribute("boardRequestDto") BoardRequestDto boardRequestDto,
                              BindingResult bindingResult) {

        // 게시글 유효성 검사
        boardService.validateBoard(boardRequestDto, bindingResult);

        if (bindingResult.hasErrors()) return "rentalee/board/createBoardForm";

        boardService.save(userDetails.getId(), boardRequestDto);

        return "redirect:/boards?notice=false";
    }

    @GetMapping("/board/{boardId}")
    public String showOneBoard(@PathVariable("boardId") Long boardId, Model model) {

        model.addAttribute("board", boardService.findById(boardId));
        model.addAttribute("replyList", replyService.findRepliesByBoardId(boardId));
        model.addAttribute("replyRequestDto", new ReplyRequestDto());

        return "rentalee/board/showOne";

    }

    @PostMapping("/board/{boardId}/like")
    public String likeBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @PathVariable("boardId") Long boardId) {

        boardService.like(userDetails.getId(), boardId);

        return "redirect:/board/" + boardId;
    }

    @GetMapping("/board/{boardId}/edit")
    public String editBoardForm(@PathVariable("boardId") Long boardId, Model model) {

        model.addAttribute("boardRequestDto", boardService.mapBoardToBoardRequestDto(boardId));

        return "rentalee/board/updateBoardForm";
    }

    @PatchMapping("/board/{boardId}")
    public String editBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                            @PathVariable("boardId") Long boardId,
                            @ModelAttribute("boardRequestDto") BoardRequestDto boardRequestDto,
                            BindingResult bindingResult) {

        boardService.validateBoard(boardRequestDto, bindingResult);

        if (bindingResult.hasErrors()) return "rentalee/board/updateBoardForm";

        boardService.edit(userDetails.getId(), boardId, boardRequestDto);

        return "redirect:/board/" + boardId;
    }

    @DeleteMapping("/board/{boardId}")
    public String deleteBoard(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @PathVariable("boardId") Long boardId) {

        boardService.delete(userDetails.getId(), boardId);

        return "redirect:/boards?notice=false";
    }

    @GetMapping("/my-page/boards")
    public String myBoardList(@AuthenticationPrincipal CustomUserDetails userDetails,
                              @RequestParam(required = false, defaultValue = "1", value = "page") int page,
                              Model model) {

        model.addAttribute("boards", boardService.findBoardsByStudentId(userDetails.getId(), page));

        return "rentalee/board/myList";
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
