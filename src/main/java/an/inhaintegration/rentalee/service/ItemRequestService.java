package an.inhaintegration.rentalee.service;

import an.inhaintegration.rentalee.domain.ItemRequest;
import an.inhaintegration.rentalee.dto.item.ItemRequestResponseDto;
import an.inhaintegration.rentalee.repository.ItemRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestRepository itemRequestRepository;

    @Transactional
    public void save(ItemRequest itemRequest) {
        itemRequestRepository.save(itemRequest);
    }

    public List<ItemRequestResponseDto> findAll() {

        Sort sort = Sort.by("id").descending();
        List<ItemRequest> itemRequests = itemRequestRepository.findAll(sort);

        return itemRequests.stream()
                .map(ItemRequest::toItemRequestResponseDto)
                .collect(Collectors.toList());
    }
}
