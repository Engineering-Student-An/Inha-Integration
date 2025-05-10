package an.inhaintegration.icross.repository;

import an.inhaintegration.icross.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findTasksBySubjectIdIn(List<Long> subjectIds);

    @Query("SELECT t FROM Task t WHERE t.deadline <= :targetDate")
    List<Task> findTasksWithDeadlineWithinThreeDays(@Param("targetDate") LocalDateTime targetDate);
}
