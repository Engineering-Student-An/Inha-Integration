package an.inhaintegration.icross.repository;

import an.inhaintegration.icross.domain.UnivInfoTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UnivInfoTaskRepository extends JpaRepository<UnivInfoTask, Long> {

    List<UnivInfoTask> findUnivInfoTasksByUnivInfoId(Long univInfoId);

    Optional<UnivInfoTask> findUnivInfoTaskByUnivInfoIdAndTaskId(Long univInfoId, Long taskId);
    boolean existsByUnivInfoIdAndTaskId(Long univInfoId, Long taskId);

    @Query("SELECT uit FROM UnivInfoTask uit " +
            "WHERE uit.completed = false AND uit.task.deadline <= :targetDate")
    List<UnivInfoTask> findIncompleteTasksDueWithinThreeDays(@Param("targetDate") LocalDateTime targetDate);

    List<UnivInfoTask> findUnivInfoTasksByCompletedIs(boolean completed);
}
