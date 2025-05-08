package an.inhaintegration.repository;

import an.inhaintegration.domain.ItemRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAll(Sort sort);

    Optional<ItemRequest> findItemRequestById(Long id);
}
