package an.inhaintegration.controller;

import an.inhaintegration.domain.Student;
import an.inhaintegration.domain.oauth2.CustomOauth2UserDetails;
import an.inhaintegration.domain.oauth2.CustomUserDetails;
import an.inhaintegration.dto.item.ItemSearchDto;
import an.inhaintegration.service.ItemRequestService;
import an.inhaintegration.service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemRequestService itemRequestService;

    @GetMapping("/item/list")
    public String list(@ModelAttribute("itemSearchDto") ItemSearchDto itemSearchDto,
                       @RequestParam(required = false, defaultValue = "1", value = "page") int page, Model model) {

        model.addAttribute("items", itemService.findItemsBySearch(page, itemSearchDto));
        return "item/list";
    }

    //
//    @GetMapping("/item/request/list")
//    public String requestList(Model model) {
//
//        model.addAttribute("requestList", itemRequestService.findAll());
//
//        return "item/requestList";
//    }
//
//    @GetMapping("/item/request")
//    public String requestForm(Model model) {
//        ItemRequestForm itemRequestForm = new ItemRequestForm();
//
//        model.addAttribute("itemRequestForm", itemRequestForm);
//        model.addAttribute("itemList", itemService.findAllItems());
//        return "item/request";
//    }
//
//    @PostMapping("/item/request")
//    public String request(@ModelAttribute ItemRequestForm itemRequestForm, BindingResult bindingResult, Model model) {
//        if(itemRequestForm.getItemName().isEmpty()) {
//            bindingResult.addError(new FieldError
//                    ("itemRequestForm", "itemName", "물품을 선택하세요"));
//        }
//        if(itemRequestForm.getContent().isEmpty()) {
//            bindingResult.addError(new FieldError
//                    ("itemRequestForm", "content", "요청 사항을 선택하세요"));
//        }
//
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("itemRequestForm", itemRequestForm);
//            return "item/request";
//        }
//
//        Student loginStudent = (Student) model.getAttribute("loginStudent");
//
//        ItemRequest itemRequest = new ItemRequest(loginStudent.getStuId(), loginStudent.getName(), itemRequestForm.getItemName(), itemRequestForm.getContent(), false);
//
//        itemRequestService.save(itemRequest);
//        return "redirect:/item/request/list";
//    }
//
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
