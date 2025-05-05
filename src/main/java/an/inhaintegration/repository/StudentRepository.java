package an.inhaintegration.repository;

import an.inhaintegration.domain.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByLoginId(String loginId);
    Optional<Student> findByStuId(String stuId);
}
