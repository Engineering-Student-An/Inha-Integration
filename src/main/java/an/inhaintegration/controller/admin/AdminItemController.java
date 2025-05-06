package an.inhaintegration.controller.admin;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomOauth2UserDetails;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import an.inhaintegration.dto.item.ItemRequestDto;
import an.inhaintegration.dto.item.ItemSearchDto;
import an.inhaintegration.service.ItemRequestService;
import an.inhaintegration.service.ItemService;
import an.inhaintegration.service.admin.AdminItemService;
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
    public String list(@ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto,
                       @RequestParam(required = false, defaultValue = "1", value = "page") int page, Model model) {

        model.addAttribute("items", itemService.findItemsBySearch(page, itemSearchDto));

        return "admin/item/list";
    }

    @GetMapping("/item")
    public String createItemForm(Model model) {

        model.addAttribute("itemRequestDto", new ItemRequestDto());

        return "admin/item/createItemForm";
    }

    @PostMapping("/item")
    public String createItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @ModelAttribute("itemRequestDto") ItemRequestDto itemRequestDto, BindingResult bindingResult) {

        // 물품 생성 요청 검증
        adminItemService.validateItemForm(itemRequestDto, bindingResult, false);

        if(bindingResult.hasErrors()) return "admin/item/createItemForm";

        // 물품 저장
        adminItemService.save(userDetails.getId(), itemRequestDto);

        return "redirect:/admin/item/list";
    }

    @GetMapping("/item/{itemId}")
    public String updateItemForm(@PathVariable("itemId") Long itemId, Model model) {

        // Item -> ItemRequestDto로 변환
        model.addAttribute("itemRequestDto", adminItemService.mapItemToItemRequestDto(itemId));

        return "admin/item/updateItemForm";
    }

    @PatchMapping("/item/{itemId}")
    public String updateItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable("itemId") String itemId,
                             @ModelAttribute("itemRequestDto") ItemRequestDto itemRequestDto, BindingResult bindingResult) {

        // 물품 수정 요청 검증
        adminItemService.validateItemForm(itemRequestDto, bindingResult, true);

        if(bindingResult.hasErrors()) return "admin/item/updateItemForm";

        // 물품 수정
        adminItemService.update(userDetails.getId(), itemRequestDto);

        return "redirect:/admin/item/list";
    }

    @DeleteMapping("/item/{itemId}")
    public String deleteItem(@AuthenticationPrincipal CustomUserDetails userDetails,
                             @PathVariable(value = "itemId") Long itemId, Model model){

        if(!adminItemService.validateDeleteItem(itemId)) {
            model.addAttribute("errorMessage", "현재 대여중인 물품은 삭제할 수 없습니다!");
            model.addAttribute("nextUrl", "/admin/item/list");
            return "error/errorMessage";
        }

        adminItemService.delete(userDetails.getId(), itemId);

        return "redirect:/admin/item/list";
    }

//    @GetMapping("/item/request/{id}/check")
//    public String checkRequest(@PathVariable Long id) {
//        itemRequestService.check(id);
//
//        return "redirect:/item/request/list";
//    }
//
//    @GetMapping("/item/request/{id}/delete")
//    public String deleteRequest(@PathVariable Long id) {
//        itemRequestService.delete(id);
//
//        return "redirect:/item/request/list";
//    }

    @ModelAttribute("loginStudent")
    public Student loginStudent() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getStudent();
        } else if (principal instanceof CustomOauth2UserDetails) {
            return ((CustomOauth2UserDetails) principal).getStudent();
        }

        return null;
    }
}
