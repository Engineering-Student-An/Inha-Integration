package an.inhaintegration.icross.controller;

import an.inhaintegration.icross.dto.QuizRequestDto;
import an.inhaintegration.icross.dto.QuizResponseWrapperDto;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/i-cross")
public class ICrossQuizController {

    @GetMapping("/quiz")
    public String quizHome(Model model) {

        model.addAttribute("quizRequestDto", new QuizRequestDto());

        return "icross/quiz/quiz";
    }

    @GetMapping("/quiz/loading")
    public String loading() {

        System.out.println("로딩 창 왔음!");
        return "icross/quiz/loadingQuiz";
    }


    @GetMapping("/quizs")
    public String listQuiz(HttpSession session, Model model) {

        model.addAttribute("quizList", (QuizResponseWrapperDto) session.getAttribute("quizList"));

        return "icross/quiz/quizList";
    }
}
