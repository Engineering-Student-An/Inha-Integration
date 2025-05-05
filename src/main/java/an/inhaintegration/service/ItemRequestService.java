package an.inhaintegration.service;

import an.inhaintegration.domain.ItemRequest;
import an.inhaintegration.exception.ItemRequestNotFoundException;
import an.inhaintegration.repository.ItemRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public void save(ItemRequest itemRequest) {
        itemRequestRepository.save(itemRequest);
    }

//    public List<ItemRequest> findAll() {
//        return itemRequestRepository.findAllByOrderByIdDesc();
//    }

    public ItemRequest findById(Long id) {
        return itemRequestRepository.findItemRequestById(id).orElseThrow(ItemRequestNotFoundException::new);
    }

    @Transactional
    public void check(Long id) {
        findById(id).check();
    }

    @Transactional
    public void delete(Long id) {
        itemRequestRepository.delete(findById(id));
    }
}
