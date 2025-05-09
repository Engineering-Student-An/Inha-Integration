package an.inhaintegration.rentalee.service.admin;

import an.inhaintegration.rentalee.domain.ItemRequest;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.domain.StudentRole;
import an.inhaintegration.rentalee.exception.ItemRequestNotFoundException;
import an.inhaintegration.rentalee.exception.StudentNotAuthorizedException;
import an.inhaintegration.rentalee.exception.StudentNotFoundException;
import an.inhaintegration.rentalee.repository.ItemRequestRepository;
import an.inhaintegration.rentalee.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminItemRequestService {

    private final StudentRepository studentRepository;
    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public void check(Long studentId, Long itemRequestId) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        // 관리자 권환 검증
        if(loginStudent.getRole() != StudentRole.ADMIN) throw new StudentNotAuthorizedException();

        ItemRequest itemRequest = itemRequestRepository.findById(itemRequestId).orElseThrow(ItemRequestNotFoundException::new);

        itemRequest.check();
    }
}
