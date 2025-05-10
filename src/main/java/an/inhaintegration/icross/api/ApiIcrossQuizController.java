package an.inhaintegration.icross.api;

import an.inhaintegration.icross.dto.QuizRequestDto;
import an.inhaintegration.icross.service.OpenAIService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/i-cross")
@RequiredArgsConstructor
public class ApiIcrossQuizController {

    private final OpenAIService openAIService;

    @PostMapping("/quiz/loading")
    public ResponseEntity<String> loadingQuiz(@ModelAttribute("quizRequestDto") QuizRequestDto quizRequestDto,
                                              HttpSession session) {

        try {
            byte[] fileData = quizRequestDto.getLectureNote().getBytes();
            session.setAttribute("lectureNoteData", fileData);
            session.setAttribute("quizRequestDto", quizRequestDto);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("파일 처리 오류");
        }

        // 리다이렉트 URL을 반환
        return ResponseEntity.status(HttpStatus.FOUND).header("Location", "/i-cross/quiz/loading").build();
    }

    @PostMapping("/quiz")
    public ResponseEntity<Map<String, String>> createQuiz(HttpSession session) {

        Map<String, String> response = new HashMap<>();

        try {
            session.setAttribute("quizList", openAIService.quiz((QuizRequestDto) session.getAttribute("quizRequestDto"), (byte[]) session.getAttribute("lectureNoteData")));
            response.put("nextUrl", "/i-cross/quizs");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("nextUrl", "/i-cross/quiz");
            response.put("message", "예상 문제 생성 중 오류가 발생했습니다!\n다시 시도해주세요!\n");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
