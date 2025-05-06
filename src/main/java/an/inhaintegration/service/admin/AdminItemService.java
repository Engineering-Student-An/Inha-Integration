package an.inhaintegration.service.admin;

import an.inhaintegration.domain.Item;
import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.StudentRole;
import an.inhaintegration.dto.item.ItemRequestDto;
import an.inhaintegration.exception.ItemNotFoundException;
import an.inhaintegration.exception.StudentNotAuthorizedException;
import an.inhaintegration.exception.StudentNotFoundException;
import an.inhaintegration.repository.ItemRepository;
import an.inhaintegration.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

@Service
@RequiredArgsConstructor
public class AdminItemService {

    private final ItemRepository itemRepository;
    private final StudentRepository studentRepository;

    @Transactional
    public void save(Long studentId, ItemRequestDto itemRequestDto){

        validateAdmin(studentId);

        Item item = Item.builder()
                .name(itemRequestDto.getName())
                .category(itemRequestDto.getCategory())
                .allStockQuantity(itemRequestDto.getAllStockQuantity())
                .stockQuantity(itemRequestDto.getAllStockQuantity())
                .rentalCount(0).build();

        itemRepository.save(item);
    }

    @Transactional
    public void update(Long studentId, ItemRequestDto itemRequestDto){

        validateAdmin(studentId);

        Item item = itemRepository.findById(itemRequestDto.getId()).orElseThrow(ItemNotFoundException::new);

        item.updateInfo(itemRequestDto.getName(), itemRequestDto.getAllStockQuantity(), itemRequestDto.getCategory());
    }

    @Transactional
    public void delete(Long studentId, Long itemId) {

        validateAdmin(studentId);

        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);

        itemRepository.delete(item);
    }

    // 물품 요청 폼 검증 메서드
    public void validateItemForm(ItemRequestDto itemRequestDto, BindingResult bindingResult, boolean isUpdate) {

        // 카테고리가 빈 경우
        if (itemRequestDto.getCategory().isEmpty()) {
            bindingResult.addError(new FieldError("itemRequestDto", "category", "카테고리를 입력해주세요!"));
        }
        // 이름이 빈 경우
        if (itemRequestDto.getName().isEmpty()) {
            bindingResult.addError(new FieldError("itemRequestDto", "name", "이름을 입력해주세요!"));
        }
        // 이름이 중복인 경우
        if(itemRepository.existsByName(itemRequestDto.getName())) {
            bindingResult.addError(new FieldError("itemRequestDto", "name", "해당 이름은 이미 존재합니다!"));
        }
        if (itemRequestDto.getAllStockQuantity() < 0) { // 재고가 0 미만인 경우
            bindingResult.addError(new FieldError("itemRequestDto", "allStockQuantity", "0 이상의 재고 수량을 입력해주세요!"));
        } else if (isUpdate && itemRequestDto.getAllStockQuantity() < itemRequestDto.getIngStockQuantity()) {   // 수정 폼이면서 대여 재고가 전체 재고보다 큰 경우
            bindingResult.addError(new FieldError("itemRequestDto", "allStockQuantity", "대여중인 수량 이상의 재고 수량을 입력해주세요!"));
        }
    }

    // 물품 제거 검증 메서드
    public boolean validateDeleteItem(Long itemId) {

        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);

        return item.getStockQuantity() == item.getAllStockQuantity();
    }

    // 관리자 권한 검증 메서드
    public void validateAdmin(Long studentId) {

        Student loginStudent = studentRepository.findById(studentId).orElseThrow(StudentNotFoundException::new);

        // 관리자 권한인지 검증
        if(loginStudent.getRole() != StudentRole.ADMIN) {
            throw new StudentNotAuthorizedException();
        }
    }

    // Item -> ItemRequestDto 변환 메서드
    public ItemRequestDto mapItemToItemRequestDto(Long itemId) {

        Item item = itemRepository.findById(itemId).orElseThrow(ItemNotFoundException::new);

        return new ItemRequestDto(item.getId(), item.getName(),
                item.getAllStockQuantity(), item.getAllStockQuantity() - item.getStockQuantity(),
                item.getCategory());
    }
}
