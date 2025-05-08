package an.inhaintegration.service;

import an.inhaintegration.domain.Board;
import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.StudentRole;
import an.inhaintegration.dto.board.BoardRequestDto;
import an.inhaintegration.dto.board.BoardResponseDto;
import an.inhaintegration.exception.BoardNotFoundException;
import an.inhaintegration.exception.StudentNotAuthorizedException;
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
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final StudentRepository studentRepository;

    public void validateBoard(BoardRequestDto boardRequestDto, BindingResult bindingResult) {

        // 유효성 검사
        if (boardRequestDto.getTitle().isEmpty()) {
            bindingResult.addError(new FieldError("boardRequestDto", "title", "제목을 입력하세요"));
        }
        if (boardRequestDto.getTitle().length() > 20) {
            bindingResult.addError(new FieldError("boardRequestDto", "title", "20자 이내로 입력해주세요"));
        }
        if (boardRequestDto.getContent().isEmpty()) {
            bindingResult.addError(new FieldError("boardRequestDto", "content", "내용을 입력하세요"));
        }
    }

    @Transactional
    public void save(Long studentId, BoardRequestDto boardRequestDto){

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        if(boardRequestDto.isNotice()){
            if(loginStudent.getRole() != StudentRole.ADMIN) throw new StudentNotAuthorizedException();
            boardRequestDto.setTitle("[공지] " + boardRequestDto.getTitle());
        }

        Board board = Board.builder()
                .title(boardRequestDto.getTitle())
                .content(boardRequestDto.getContent())
                .notice(boardRequestDto.isNotice())
                .build();
        board.setStudent(loginStudent);

        boardRepository.save(board);
    }

    public BoardResponseDto findById(Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

        return board.toBoardResponseDto();
    }

    public BoardRequestDto mapBoardToBoardRequestDto(Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

        return board.toBoardRequestDto();
    }

    @Transactional
    public void edit(Long studentId, Long boardId, BoardRequestDto boardRequestDto) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

        // 게시글 작성자 검증
        if(!loginStudent.getId().equals(studentId)) throw new StudentNotAuthorizedException();

        board.edit(boardRequestDto.getTitle(), boardRequestDto.getContent());
    }

    @Transactional
    public void delete(Long studentId, Long boardId) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);

        // 관리자가 아니거나 게시글 작성자와 로그인 유저가 다른 경우
        if(loginStudent.getRole() != StudentRole.ADMIN && !studentId.equals(board.getStudent().getId())) throw new StudentNotAuthorizedException();

        boardRepository.delete(board);
    }

    public Page<Board> findBoardsByNotice(int page, boolean notice) {

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());

        return boardRepository.findBoardsByNoticeIs(pageRequest, notice);
    }

    // 최근 공지사항 조회 메서드
    public List<BoardResponseDto> findRecentNotice() {

        Pageable pageable = PageRequest.of(0, 5, Sort.by("createdAt").descending());
        Page<Board> boards = boardRepository.findBoardsByNoticeIs(pageable, true);

        return boards.stream()
                .map(Board::toBoardResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void like(Long studentId, Long boardId) {

        Board board = boardRepository.findById(boardId).orElseThrow(BoardNotFoundException::new);
        board.like(studentId);
    }

    public Page<Board> findBoardsByStudentId(Long studentId, int page) {

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());

        return boardRepository.findBoardsByStudentId(pageRequest, studentId);
    }
}
