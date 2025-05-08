package an.inhaintegration.service.admin;

import an.inhaintegration.domain.ItemRequest;
import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.StudentRole;
import an.inhaintegration.exception.ItemRequestNotFoundException;
import an.inhaintegration.exception.StudentNotAuthorizedException;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.ItemRequestRepository;
import an.inhaintegration.repository.StudentRepository;
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
