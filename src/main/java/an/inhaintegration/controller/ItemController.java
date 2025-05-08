package an.inhaintegration.controller;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import an.inhaintegration.dto.item.ItemRequestRequestDto;
import an.inhaintegration.dto.item.ItemSearchRequestDto;
import an.inhaintegration.service.ItemRequestService;
import an.inhaintegration.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemRequestService itemRequestService;

    @GetMapping("/items")
    public String list(@ModelAttribute("itemSearchRequestDto") ItemSearchRequestDto itemSearchRequestDto,
                       @RequestParam(required = false, defaultValue = "1", value = "page") int page, Model model) {

        model.addAttribute("items", itemService.findItemsBySearch(page, itemSearchRequestDto));
        return "item/list";
    }

    @GetMapping("/item/requests")
    public String requestList(Model model) {

        model.addAttribute("requestList", itemRequestService.findAll());

        return "item/requestList";
    }

    @GetMapping("/item/request")
    public String requestForm(Model model) {

        model.addAttribute("itemRequestRequestDto", new ItemRequestRequestDto());
        model.addAttribute("itemList", itemService.findAll());

        return "item/request";
    }

    @PostMapping("/item/request")
    public String request(@AuthenticationPrincipal CustomUserDetails userDetails,
                          @ModelAttribute("itemRequestRequestDto") ItemRequestRequestDto itemRequestRequestDto, BindingResult bindingResult) {

        // 요청 사항 유효성 검증
        itemService.validateItemRequest(itemRequestRequestDto, bindingResult);

        if (bindingResult.hasErrors()) return "item/request";

        itemService.saveItemRequest(userDetails.getId(), itemRequestRequestDto);

        return "redirect:/item/requests";
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
