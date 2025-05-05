package an.inhaintegration.service;

import an.inhaintegration.domain.Proposal;
import an.inhaintegration.repository.ProposalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProposalService {

    private final ProposalRepository proposalRepository;

    public Page<Proposal> findAll(Pageable pageable){
        return proposalRepository.findAll(pageable);
    }

    public void saveProposal(Proposal proposal) {
        proposalRepository.save(proposal);
    }

    public Proposal findById(Long id) {
        return proposalRepository.findById(id).orElseThrow();
    }
}
