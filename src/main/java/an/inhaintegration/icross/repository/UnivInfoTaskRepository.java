package an.inhaintegration.icross.repository;

import an.inhaintegration.icross.domain.UnivInfoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnivInfoTaskRepository extends JpaRepository<UnivInfoTask, Long> {

    List<UnivInfoTask> findUnivInfoTasksByUnivInfoId(Long univInfoId);

    Optional<UnivInfoTask> findUnivInfoTaskByUnivInfoIdAndTaskId(Long univInfoId, Long taskId);
    boolean existsByUnivInfoIdAndTaskId(Long univInfoId, Long taskId);
}
