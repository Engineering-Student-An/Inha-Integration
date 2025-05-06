package an.inhaintegration.repository;

import an.inhaintegration.domain.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByLoginId(String loginId);

    Optional<Student> findByStuId(String stuId);

    boolean existsByStuId(String stuId);

    // OAuth 로그인 용 회원 조회 메서드
    Optional<Student> findByLoginIdAndProvider(String email, String provider);

    Page<Student> findStudentsByStuIdContainingAndNameContaining(String stuId, String name, Pageable pageable);
}
