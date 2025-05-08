package an.inhaintegration.service;

import an.inhaintegration.domain.Item;
import an.inhaintegration.dto.item.ItemResponseDto;
import an.inhaintegration.dto.item.ItemSearchRequestDto;
import an.inhaintegration.repository.ItemRepository;
import an.inhaintegration.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final RentalRepository rentalRepository;

    public List<Item> findAll() {

        return itemRepository.findAll();
    }

    // 전체 카테고리 조회 메서드
    public List<String> findDistinctCategories () {

        return itemRepository.findDistinctCategories();
    }

    public List<ItemResponseDto> findItemsByCategory(String category) {

        List<Item> items = itemRepository.findItemsByCategory(category);

        return items.stream()
                .map(this::mapItemToItemResponseDto)
                .collect(Collectors.toList());
    }

    public ItemResponseDto mapItemToItemResponseDto(Item item) {

        return new ItemResponseDto(item.getId(), item.getName(), item.getStockQuantity(), item.getAllStockQuantity());
    }

    public Page<Item> findAllItems(Pageable pageable) { return itemRepository.findAll(pageable); }

    public Page<Item> findItemsByCategoryAndName(String category, String name, Pageable pageable) { return itemRepository.findItemsByCategoryContainingAndNameContaining(category, name, pageable); }

    public Page<Item> findItemsBySearch(int page, ItemSearchRequestDto itemSearchRequestDto) {

        // 검색 조건 추가해서 조회
        if (itemSearchRequestDto.getCategory() != null && itemSearchRequestDto.getName() != null) {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("category").and(Sort.by("name")));
            return findItemsByCategoryAndName(itemSearchRequestDto.getCategory(), itemSearchRequestDto.getName(), pageRequest);
        }

        // 모든 물품 조회
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("category").and(Sort.by("name")));
        return findAllItems(pageRequest);
    }
}