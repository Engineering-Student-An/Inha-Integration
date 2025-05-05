package an.inhaintegration.repository;

import an.inhaintegration.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findItemsByCategoryContainingAndNameContaining(String category, String name, Pageable pageable);
}
