package an.inhaintegration.service;

import an.inhaintegration.domain.Board;
import an.inhaintegration.domain.Reply;
import an.inhaintegration.domain.Student;
import an.inhaintegration.exception.BoardNotFoundException;
import an.inhaintegration.repository.BoardRepository;
import an.inhaintegration.repository.ReplyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
public class ReplyService {

    private final ReplyRepository replyRepository;
    private final BoardRepository boardRepository;

    @Transactional
    public Reply reply(Student student, Long boardId, String content) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        Reply reply = Reply.builder().content(content).build();
        reply.setBoard(board);
        reply.setStudent(student);
        return replyRepository.save(reply);
    }

    @Transactional
    public void like(Long id, String stuId){
        Reply reply = replyRepository.findReplyById(id);
        reply.like(stuId);
    }

    @Transactional
    public void delete(Long id) {
        replyRepository.delete(replyRepository.findReplyById(id));
    }

//    public List<Reply> findRepliesByStuId(String stuId) {
//        return replyRepository.findRepliesByStuId(stuId);
//    }

}
