package an.inhaintegration.service;

import an.inhaintegration.domain.Item;
import an.inhaintegration.domain.Rental;
import an.inhaintegration.domain.RentalStatus;
import an.inhaintegration.domain.Student;
import an.inhaintegration.dto.rental.RentalResponseDto;
import an.inhaintegration.dto.rental.RentalSearchRequestDto;
import an.inhaintegration.exception.ItemNotFoundException;
import an.inhaintegration.exception.RentalNotFoundException;
import an.inhaintegration.exception.RentalOverdueException;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.ItemRepository;
import an.inhaintegration.repository.RentalRepository;
import an.inhaintegration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
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
//    private final RentalQueryRepository rentalQueryRepository;

    // 대여 시작
    @Transactional
    public void save(Long studentId, Long itemId) {

        // 학생 조회
        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        // 물품 조회
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);

        // 대여 생성
        Rental rental = new Rental();
        rental.createRental(student, item);

        // 대여 저장
        rentalRepository.save(rental);
    }

    // 대여 종료
    @Transactional
    public void finishRental(Long rentalId) {

        Rental rental = rentalRepository.findById(rentalId).orElseThrow(RentalNotFoundException::new);

        // 연체 중인 대여인지 리턴
        if(ChronoUnit.DAYS.between(rental.getRentalDate(), LocalDate.now()) > 3) throw new RentalOverdueException();

        rental.finishRental();
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



    // Rental -> RentalResponseDto 변환 메서드
    private RentalResponseDto mapRentalToRentalResponseDto(Rental rental) {

        return new RentalResponseDto(rental.getId(), rental.getStatus(), rental.getItem().getName(), rental.getItem().getCategory(), rental.getRentalDate());
    }

    public Page<Rental> findRentalsBySearch(Long studentId, RentalSearchRequestDto rentalSearchRequestDto, int page) {

        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("status").and(Sort.by("rentalDate")));

        // 전체 대여 조회
        if(rentalSearchRequestDto.getRentalStatus() == null){

            return rentalRepository.findRentalsByStudentId(studentId, pageRequest);
        } else{ // 대여 상태에 따라 조회
            Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
            rentalSearchRequestDto.setStuId(student.getStuId());

            return rentalRepository.findRentalsByStatusAndStudentId(rentalSearchRequestDto.getRentalStatus(), studentId, pageRequest);
        }

    }
}