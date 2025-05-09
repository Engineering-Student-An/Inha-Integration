package an.inhaintegration.rentalee.repository;

import an.inhaintegration.rentalee.domain.Reply;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository  extends JpaRepository<Reply, Long> {

    List<Reply> findRepliesByBoardId(Long boardId, Sort sort);
}
