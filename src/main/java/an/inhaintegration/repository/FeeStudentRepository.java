package an.inhaintegration.repository;

import an.inhaintegration.domain.FeeStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeeStudentRepository extends JpaRepository<FeeStudent, Long> {

    Page<FeeStudent> findFeeStudentsByStuIdContainingAndNameContaining(String stuId, String name, Pageable pageable);

    Optional<FeeStudent> findByStuId(String stuId);

    boolean existsByStuId(String stuId);
}
