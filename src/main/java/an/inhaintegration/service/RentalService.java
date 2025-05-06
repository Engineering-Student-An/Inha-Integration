package an.inhaintegration.service;

import an.inhaintegration.domain.Rental;
import an.inhaintegration.domain.RentalStatus;
import an.inhaintegration.dto.RentalSearch;
import an.inhaintegration.dto.rental.RentalResponseDto;
import an.inhaintegration.exception.RentalNotFoundException;
import an.inhaintegration.repository.ItemRepository;
import an.inhaintegration.repository.RentalQueryRepository;
import an.inhaintegration.repository.RentalRepository;
import an.inhaintegration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RentalService {

    private final RentalRepository rentalRepository;
    private final StudentRepository studentRepository;
    private final ItemRepository itemRepository;
    private final RentalQueryRepository rentalQueryRepository;

    // 대여 시작
//    @Transactional
//    public Long rental(String stuId, Long itemId) {
//        // 엔티티 조회
//        Student student = studentRepository.findByStuId(stuId);
//        Item item = itemRepository.findItemById(itemId);
//
//        // 대여 생성
//        Rental rental = Rental.createRental(student, item);
//
//        // rentalCount 증가
//        item.setRentalCount(item.getRentalCount() + 1);
//
//        // 대여 저장
//        rentalRepository.save(rental);
//
//        return rental.getId();
//    }

    // 대여 종료
    @Transactional
    public void finishRental(Long rentalId) {
        Rental rental = rentalRepository.findById(rentalId).orElseThrow(RentalNotFoundException::new);
        rental.finishRental();
    }

    public Rental findRentalByRentalId(Long rentalId){
        return rentalRepository.findOneById(rentalId);
    }

    // 특정 학생의 렌탈 리스트 검색
    public Page<Rental> findMyRentalList(Long id, Pageable pageable) {
        return rentalRepository.findRentalsByStudent_Id(id, pageable);
    }

    public List<RentalResponseDto> findMyRentalINGList(Long studentId) {

        if(studentId == null) return null;

        // 연체, 대여 중 상태를 리스트화
        Collection<RentalStatus> collection = Arrays.asList(RentalStatus.OVERDUE, RentalStatus.ING);
        List<Rental> rentals = rentalRepository.findRentalsByStudentIdAndStatusIn(studentId, collection);

        return rentals.stream()
                .map(this::mapRentalToRentalResponseDto)
                .collect(Collectors.toList());
    }

    // 특정 학생의 특정 아이템 렌탈 리스트 검색
    public Boolean existsByStudentIdAndItemId(Long studentId, Long itemId) {
        Collection<RentalStatus> collection = new ArrayList<>();
        collection.add(RentalStatus.FINISH);
        collection.add(RentalStatus.FINISH_OVERDUE);

        return rentalRepository.existsByStudent_IdAndItemIdAndStatusNotIn(studentId, itemId, collection);
    }

    // 대여 리스트 검색
//    public Page<Rental> findRentals(RentalSearch rentalSearch, Pageable pageable) {
//        Student student = studentRepository.findByStuId(rentalSearch.getStuId());
//        Long id = null;
//        if(student != null) id = student.getId();
//
//        return rentalRepository.findRentalsByStatusAndStudent_Id(rentalSearch.getRentalStatus(), id, pageable);
//    }

    public Page<Rental> findAll(Pageable pageable){
        return rentalRepository.findAll(pageable);
    }

    public Page<Rental> findRentalByStuId_Status_Item(RentalSearch rentalSearch, Pageable pageable) {
        return rentalQueryRepository.findByStuIdAndStatusAndItemName(rentalSearch, pageable);

    }

    // Rental -> RentalResponseDto 변환 메서드
    private RentalResponseDto mapRentalToRentalResponseDto(Rental rental) {

        return new RentalResponseDto(rental.getId(), rental.getStatus(), rental.getItem().getName(), rental.getItem().getCategory(), rental.getRentalDate());
    }
}