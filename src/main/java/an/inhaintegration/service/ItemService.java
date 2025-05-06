package an.inhaintegration.service;

import an.inhaintegration.domain.Item;
import an.inhaintegration.dto.ItemSearch;
import an.inhaintegration.exception.ItemNotFoundException;
import an.inhaintegration.repository.ItemRepository;
import an.inhaintegration.repository.RentalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final RentalRepository rentalRepository;

    @Transactional
    public void saveItem(Item item){
        itemRepository.save(item);
    }

    @Transactional
    public void updateItem(Long itemId, String name, int allStockQuantity, String category){
        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        item.updateInfo(name, allStockQuantity, category);
    }

    @Transactional
    public void deleteItem(Long itemId){

        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);
        itemRepository.delete(item);
    }

    public List<Item> findAllItems() {
        return itemRepository.findAll();
    }

    public Set<String> findCategories () {

        Set<String> categories = new HashSet<>();

        for (Item item : itemRepository.findAll()) {
            categories.add(item.getCategory());
        }

        return categories;
    }

//    public List<Item> findItemsByCategory(String category) {
//        return itemRepository.findItemByCategory(category);
//    }

    public Page<Item> findAllItems(Pageable pageable) { return itemRepository.findAll(pageable); }

    public Page<Item> findItemsByCategoryAndName(String category, String name, Pageable pageable) { return itemRepository.findItemsByCategoryContainingAndNameContaining(category, name, pageable); }

    public List<Item> findHotItems() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("rentalCount").descending());
        return itemRepository.findAll(pageable).getContent();
    }


    public Page<Item> findItemsBySearch(int page, ItemSearch itemSearch) {

        // 검색 조건 추가해서 조회
        if (itemSearch.getCategory() != null && itemSearch.getName() != null) {
            PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("category").and(Sort.by("name")));
            return findItemsByCategoryAndName(itemSearch.getCategory(), itemSearch.getName(), pageRequest);
        }

        // 모든 물품 조회
        PageRequest pageRequest = PageRequest.of(page - 1, 10, Sort.by("category").and(Sort.by("name")));
        return findAllItems(pageRequest);
    }
}
