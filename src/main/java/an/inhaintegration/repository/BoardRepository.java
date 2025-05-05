package an.inhaintegration.repository;

import an.inhaintegration.domain.Board;
import an.inhaintegration.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    List<Board> findBoardsByStudent(Student student);
    Page<Board> findBoardsByStudent(Pageable pageable, Student student);

    Page<Board> findBoardsByNoticeIs(Pageable pageable, boolean notice);
}
