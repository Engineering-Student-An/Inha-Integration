package an.inhaintegration.service;

import an.inhaintegration.domain.*;
import an.inhaintegration.dto.reply.ReplyRequestDto;
import an.inhaintegration.dto.reply.ReplyResponseDto;
import an.inhaintegration.exception.BoardNotFoundException;
import an.inhaintegration.exception.ReplyNotFoundException;
import an.inhaintegration.exception.StudentNotAuthorizedException;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.BoardRepository;
import an.inhaintegration.repository.ReplyRepository;
import an.inhaintegration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void like(Long studentId, Long replyId){

        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
        reply.like(studentId);
    }

    @Transactional
    public void delete(Long studentId, Long replyId) {

        Reply reply = replyRepository.findById(replyId).orElseThrow(ReplyNotFoundException::new);
        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        // 로그인 회원이 관리자이거나 댓글 작성자인지 검증
        if(loginStudent.getRole() != StudentRole.ADMIN && !reply.getStudent().getId().equals(studentId)) throw new StudentNotAuthorizedException();

        replyRepository.delete(reply);
    }

    public void validateReply(ReplyRequestDto replyRequestDto, BindingResult bindingResult) {

        if(!StringUtils.hasText(replyRequestDto.getContent())) {
            bindingResult.addError(new FieldError("replyRequestDto", "content", "내용을 입력하세요"));
        }
    }

    @Transactional
    public void save(Long studentId, Long boardId, ReplyRequestDto replyRequestDto) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

        Reply reply = Reply.builder().content(replyRequestDto.getContent()).build();
        reply.setStudent(loginStudent);
        reply.setBoard(board);

        replyRepository.save(reply);
    }

    public List<ReplyResponseDto> findRepliesByBoardId(Long boardId) {

        List<Reply> replyList = replyRepository.findRepliesByBoardId(boardId);
        return replyList.stream()
                .map(Reply::toReplyResponseDto)
                .collect(Collectors.toList());
    }

//    public List<Reply> findRepliesByStuId(String stuId) {
//        return replyRepository.findRepliesByStuId(stuId);
//    }

}
