package an.inhaintegration.rentalee.service.admin;

import an.inhaintegration.rentalee.domain.Rental;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.dto.rental.RentalSearchRequestDto;
import an.inhaintegration.rentalee.exception.StudentNotFoundException;
import an.inhaintegration.rentalee.repository.RentalQueryRepository;
import an.inhaintegration.rentalee.repository.RentalRepository;
import an.inhaintegration.rentalee.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminRentalService {

    private final RentalRepository rentalRepository;
    private final RentalQueryRepository rentalQueryRepository;
    private final StudentRepository studentRepository;

    public Page<Rental> findRentalsBySearch(Long studentId, RentalSearchRequestDto rentalSearchRequestDto, int page) {

        Student student = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        // dto에 해당 학생 학번 저장
        if(rentalSearchRequestDto.getStuId() == null) rentalSearchRequestDto.setStuId(student.getStuId());

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("status").and(Sort.by("rentalDate")));

        if(rentalSearchRequestDto.getRentalStatus() == null){
            return rentalRepository.findRentalsByStudentId(student.getId(), pageRequest);
        } else{
            return rentalRepository.findRentalsByStatusAndStudentId(rentalSearchRequestDto.getRentalStatus(), student.getId(), pageRequest);
        }
    }

    public Page<Rental> findRentalsBySearch(RentalSearchRequestDto rentalSearchRequestDto, int page) {

        PageRequest pageRequest = PageRequest.of(page-1, 10, Sort.by("rentalDate").descending().and(Sort.by("status").descending()));

        return rentalQueryRepository.findByStuIdAndStatusAndItemName(rentalSearchRequestDto, pageRequest);
    }
}
