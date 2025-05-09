package an.inhaintegration.rentalee.repository;

import an.inhaintegration.rentalee.domain.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    Page<Item> findItemsByCategoryContainingAndNameContaining(String category, String name, Pageable pageable);

    boolean existsByName(String name);

    @Query("SELECT DISTINCT i.category FROM Item i")
    List<String> findDistinctCategories();

    List<Item> findItemsByCategory(String category);
}
