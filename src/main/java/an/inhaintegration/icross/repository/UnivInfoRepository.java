package an.inhaintegration.icross.repository;

import an.inhaintegration.icross.domain.UnivInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnivInfoRepository extends JpaRepository<UnivInfo, Long> {

    Optional<UnivInfo> findByStudentId(Long studentId);
}
