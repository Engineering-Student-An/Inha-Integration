//package an.inhaintegration.rentalee.service;
//
//
//import an.inhaintegration.rentalee.domain.StudentRole;
//import an.inhaintegration.rentalee.dto.student.StudentRequestDto;
//import lombok.RequiredArgsConstructor;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//@RequiredArgsConstructor
//@Component
//public class InsertAdmin implements CommandLineRunner {
//
//    private final StudentService studentService;
//    private final ItemService itemService;
//    private final RentalService rentalService;
//
//    // 스프링 띄워지면서 자동으로 실행됨 => 일단은 관리자랑 몇개 데이터 추가
//    @Override
//    public void run(String... args) {
//        StudentRequestDto joinRequest = new StudentRequestDto();
//        joinRequest.setName("관리자");
//        joinRequest.setRole(StudentRole.ADMIN);
//        joinRequest.setLoginId("ADMIN");
//        joinRequest.setStuId("ADMIN");
//        joinRequest.setPassword("ADMIN");
//        joinRequest.setPasswordCheck("ADMIN");
//        studentService.join(joinRequest, "chm20060@gmail.com");
//
//
////        Rule announcement = new Rule("필독!", "규칙1. ㅅㄱ");
////        ruleRepository.save(announcement);
//
//    }
//}
