package an.inhaintegration.service;

import an.inhaintegration.domain.Proposal;
import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.StudentRole;
import an.inhaintegration.dto.proposal.ProposalRequestDto;
import an.inhaintegration.dto.proposal.ProposalResponseDto;
import an.inhaintegration.exception.ProposalNotFoundException;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.ProposalRepository;
import an.inhaintegration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;
    private final StudentRepository studentRepository;

    public Page<Proposal> findAll(int page){

        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("createdAt").descending());

        return proposalRepository.findAll(pageRequest);
    }

    public void validateProposal(ProposalRequestDto proposalRequestDto, BindingResult bindingResult) {

        if (proposalRequestDto.getTitle().isEmpty()) {
            bindingResult.addError(new FieldError("proposalRequestDto", "title", "제목을 입력하세요"));
        }
        if (proposalRequestDto.getTitle().length() > 20) {
            bindingResult.addError(new FieldError("proposalRequestDto", "title", "20자 이내로 입력해주세요"));
        }
        if (proposalRequestDto.getContent().isEmpty()) {
            bindingResult.addError(new FieldError("proposalRequestDto", "content", "내용을 입력하세요"));
        }
    }

    @Transactional
    public void create(Long studentId, ProposalRequestDto proposalRequestDto) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        Proposal proposal = Proposal.builder()
                .title(proposalRequestDto.getTitle())
                .content(proposalRequestDto.getContent())
                .isSecret(proposalRequestDto.isSecret())
                .build();

        proposal.setStudent(loginStudent);

        proposalRepository.save(proposal);
    }

    // 접근 권한 체크 메서드
    public void checkAccess(Long studentId, Long proposalId) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow(ProposalNotFoundException::new);

        if(proposal.isSecret() && loginStudent.getRole() != StudentRole.ADMIN && !proposal.getStudent().getId().equals(studentId)) {
            throw new AccessDeniedException("접근 권한이 없습니다!");
        }
    }

    public ProposalResponseDto findById(Long proposalId) {

        Proposal proposal = proposalRepository.findById(proposalId).orElseThrow(ProposalNotFoundException::new);
        return proposal.toProposalResponseDto();
    }
}
