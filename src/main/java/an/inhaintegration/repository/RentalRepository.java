package an.inhaintegration.repository;

import an.inhaintegration.domain.Rental;
import an.inhaintegration.domain.RentalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {

    // rental_id 로 Rental 하나 조회
    Rental findOneById(Long id);

    List<Rental> findRentalsByStatus(RentalStatus rentalStatus);



    Boolean existsByStudent_IdAndItemIdAndStatusNotIn(Long student_id, Long item_id, Collection<RentalStatus> status);

    // student로 여러개 조회
    Page<Rental> findRentalsByStudentId(Long studentId, Pageable pageable);

    // rentalStatus + studentId로 여러개 조회
    Page<Rental> findRentalsByStatusAndStudentId(RentalStatus rentalStatus, Long studentId, Pageable pageable);

    void deleteRentalByItem_Id(Long itemId);

    boolean existsByStudent_IdAndStatusNotIn(Long id, Collection<RentalStatus> status);

    List<Rental> findRentalsByStudentIdAndStatusIn(Long id, Collection<RentalStatus> status);
}
