package an.inhaintegration.rentalee.service;

import an.inhaintegration.rentalee.domain.Item;
import an.inhaintegration.rentalee.domain.ItemRequest;
import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.rentalee.dto.item.ItemRequestRequestDto;
import an.inhaintegration.rentalee.dto.item.ItemResponseDto;
import an.inhaintegration.rentalee.dto.item.ItemSearchRequestDto;
import an.inhaintegration.rentalee.exception.ItemNotFoundException;
import an.inhaintegration.rentalee.exception.StudentNotFoundException;
import an.inhaintegration.rentalee.repository.ItemRepository;
import an.inhaintegration.rentalee.repository.ItemRequestRepository;
import an.inhaintegration.rentalee.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final StudentRepository studentRepository;
    private final ItemRequestRepository itemRequestRepository;

    public List<ItemResponseDto> findAll() {

        List<Item> items = itemRepository.findAll();
        return items.stream()
                .map(Item::toItemResponseDto)
                .collect(Collectors.toList());
    }

    // 전체 카테고리 조회 메서드
    public List<String> findDistinctCategories () {

        return itemRepository.findDistinctCategories();
    }

    public List<ItemResponseDto> findItemsByCategory(String category) {

        List<Item> items = itemRepository.findItemsByCategory(category);

        return items.stream()
                .map(Item::toItemResponseDto)
                .collect(Collectors.toList());
    }

    public Page<Item> findItemsByCategoryAndName(String category, String name, Pageable pageable) { return itemRepository.findItemsByCategoryContainingAndNameContaining(category, name, pageable); }

    public Page<Item> findItemsBySearch(int page, ItemSearchRequestDto itemSearchRequestDto) {

        // 검색 조건 추가해서 조회
        if (itemSearchRequestDto.getCategory() != null && itemSearchRequestDto.getName() != null) {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("category").and(Sort.by("name")));
            return findItemsByCategoryAndName(itemSearchRequestDto.getCategory(), itemSearchRequestDto.getName(), pageRequest);
        }

        // 모든 물품 조회
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("category").and(Sort.by("name")));
        return itemRepository.findAll(pageRequest);
    }

    public void validateItemRequest(ItemRequestRequestDto itemRequestRequestDto, BindingResult bindingResult) {

        if(itemRequestRequestDto.getItemId() == null) {
            bindingResult.addError(new FieldError("itemRequestRequestDto", "itemId", "물품을 선택하세요!"));
        }
        if(itemRequestRequestDto.getContent().isEmpty()) {
            bindingResult.addError(new FieldError("itemRequestRequestDto", "content", "요청 사항을 입력하세요!"));
        }
    }

    @Transactional
    public void saveItemRequest(Long studentId, ItemRequestRequestDto itemRequestRequestDto) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);
        Item item = itemRepository.findById(itemRequestRequestDto.getItemId()).orElseThrow(ItemNotFoundException::new);

        ItemRequest itemRequest = ItemRequest.builder()
                .content(itemRequestRequestDto.getContent())
                .checked(false).build();
        itemRequest.setItem(item);
        itemRequest.setStudent(loginStudent);

        itemRequestRepository.save(itemRequest);
    }
}