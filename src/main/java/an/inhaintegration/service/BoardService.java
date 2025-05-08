package an.inhaintegration.service;

import an.inhaintegration.domain.Board;
import an.inhaintegration.domain.Student;
import an.inhaintegration.dto.BoardForm;
import an.inhaintegration.dto.board.BoardResponseDto;
import an.inhaintegration.exception.BoardNotFoundException;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.BoardRepository;
import an.inhaintegration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void save(Board board){
        boardRepository.save(board);
    }

    @Transactional
    public void like(Long boardId, String stuId) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        board.like(stuId);
    }

    @Transactional
    public void delete(Long boardId) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        boardRepository.delete(board);
    }

    @Transactional
    public void edit(Long boardId, BoardForm boardForm) {
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        board.edit(boardForm);
    }

    public Page<Board> findAll(Pageable pageable) {
        return boardRepository.findAll(pageable);
    }

    public Page<Board> findByNotice(Pageable pageable, boolean notice) {
        return boardRepository.findBoardsByNoticeIs(pageable, notice);
    }

    public Page<Board> findByStudent(Pageable pageable, String stuId) {
        Student student = studentRepository.findByStuId(stuId).orElseThrow(StudentNotFoundException::new);
        return boardRepository.findBoardsByStudent(pageable, student);
    }

    public List<Board> findBoardsByStudent(String stuId) {
        Student student = studentRepository.findByStuId(stuId).orElseThrow(StudentNotFoundException::new);
        return boardRepository.findBoardsByStudent(student);
    }

    // 최근 공지사항 조회 메서드
    public List<BoardResponseDto> findRecentNotice() {

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Board> boards = boardRepository.findBoardsByNoticeIs(pageable, true);

        return boards.stream()
                .map(Board::toBoardResponseDto)
                .collect(Collectors.toList());
    }
}
