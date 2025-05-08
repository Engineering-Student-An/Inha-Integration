package an.inhaintegration.repository;

import an.inhaintegration.domain.Reply;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyRepository  extends JpaRepository<Reply, Long> {

    List<Reply> findRepliesByBoardId(Long boardId);
}
