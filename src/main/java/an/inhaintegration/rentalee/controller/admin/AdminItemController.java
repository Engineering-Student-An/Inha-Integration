package an.inhaintegration.rentalee.controller.admin;

import an.inhaintegration.rentalee.domain.Student;
import an.inhaintegration.oauth2.CustomUserDetails;
import an.inhaintegration.rentalee.dto.item.ItemRequestDto;
import an.inhaintegration.rentalee.dto.item.ItemSearchRequestDto;
import an.inhaintegration.rentalee.service.ItemRequestService;
import an.inhaintegration.rentalee.service.ItemService;
import an.inhaintegration.rentalee.service.admin.AdminItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminItemController {

    private final ItemService itemService;
    private final ItemRequestService itemRequestService;
    private final AdminItemService adminItemService;

    @GetMapping("/items")
    public String list(@ModelAttribute("itemSearchRequestDto") ItemSearchRequestDto itemSearchRequestDto,
                       @RequestParam(required = false, defaultValue = "1", value = "page") int page, Model model) {

        model.addAttribute("items", itemService.findItemsBySearch(page, itemSearchRequestDto));

        return "rentalee/admin/item/list";
    }

    @GetMapping("/item")
    public String createItemForm(Model model) {

        model.addAttribute("itemRequestDto", new ItemRequestDto());

        return "rentalee/admin/item/createItemForm";
    }

    @PostMapping("/item")
    public String createItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @ModelAttribute("itemRequestDto") ItemRequestDto itemRequestDto, BindingResult bindingResult) {

        // 물품 생성 요청 검증
        adminItemService.validateItemForm(itemRequestDto, bindingResult, false);

        if(bindingResult.hasErrors()) return "rentalee/admin/item/createItemForm";

        // 물품 저장
        adminItemService.save(userDetails.getId(), itemRequestDto);

        return "redirect:/admin/items";
    }

    @GetMapping("/item/{itemId}")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {

        // Item -> ItemRequestDto로 변환
        model.addAttribute("itemRequestDto", adminItemService.mapItemToItemRequestDto(itemId));

        return "rentalee/admin/item/updateItemForm";
    }

    @PatchMapping("/item/{itemId}")
    public String updateItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("itemId") Long itemId,
                             @ModelAttribute("itemRequestDto") ItemRequestDto itemRequestDto, BindingResult bindingResult) {

        // 물품 수정 요청 검증
        adminItemService.validateItemForm(itemRequestDto, bindingResult, true);

        if(bindingResult.hasErrors()) return "rentalee/admin/item/updateItemForm";

        // 물품 수정
        adminItemService.update(userDetails.getId(), itemRequestDto);

        return "redirect:/admin/items";
    }

    @DeleteMapping("/item/{itemId}")
    public String deleteItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable(value = "itemId") Long itemId, Model model){

        if(!adminItemService.validateDeleteItem(itemId)) {
            model.addAttribute("errorMessage", "현재 대여중인 물품은 삭제할 수 없습니다!");
            model.addAttribute("nextUrl", "/admin/items");
            return "error/errorMessage";
        }

        adminItemService.delete(userDetails.getId(), itemId);

        return "redirect:/admin/items";
    }

    @ModelAttribute("loginStudent")
    public Student loginStudent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getStudent();
        }

        return null;
    }
}
