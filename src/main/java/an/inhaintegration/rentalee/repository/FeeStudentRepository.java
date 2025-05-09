package an.inhaintegration.rentalee.repository;

import an.inhaintegration.rentalee.domain.FeeStudent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FeeStudentRepository extends JpaRepository<FeeStudent, Long> {

    Page<FeeStudent> findFeeStudentsByStuIdContainingAndNameContaining(String stuId, String name, Pageable pageable);

    Optional<FeeStudent> findByStuId(String stuId);

    boolean existsByStuId(String stuId);

    @Modifying
    @Query("DELETE FROM FeeStudent f WHERE f.stuId IN :stuIds")
    void deleteByStuIdIn(@Param("stuIds")List<String> stuIds);
}
