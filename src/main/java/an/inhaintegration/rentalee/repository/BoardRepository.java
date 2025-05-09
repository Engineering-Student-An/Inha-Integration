package an.inhaintegration.rentalee.repository;

import an.inhaintegration.rentalee.domain.Board;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    Page<Board> findBoardsByStudentId(Pageable pageable, Long studentId);

    Page<Board> findBoardsByNoticeIs(Pageable pageable, boolean notice);
}
