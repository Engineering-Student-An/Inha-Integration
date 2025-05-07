package an.inhaintegration.service.admin;

import an.inhaintegration.domain.Rental;
import an.inhaintegration.domain.Student;
import an.inhaintegration.dto.rental.RentalSearchRequestDto;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.RentalRepository;
import an.inhaintegration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminRentalService {

    private final RentalRepository rentalRepository;
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
}
