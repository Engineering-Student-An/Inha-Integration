package an.inhaintegration.icross.repository;

import an.inhaintegration.icross.domain.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

    List<Task> findTasksBySubjectIdIn(List<Long> subjectIds);
}
