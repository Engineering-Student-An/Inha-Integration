package an.inhaintegration.icross.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuizResponseWrapperDto {

    List<QuizResponseDto> oxQuestions;
    List<QuizResponseDto> multipleChoiceQuestions;
    List<QuizResponseDto> shortAnswerQuestions;
    List<String> answers;
}