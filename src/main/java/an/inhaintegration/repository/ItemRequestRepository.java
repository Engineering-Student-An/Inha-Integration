package an.inhaintegration.repository;

import an.inhaintegration.domain.ItemRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

//    List<ItemRequest> findAllByOrderByIdDesc();

    Optional<ItemRequest> findItemRequestById(Long id);
}
