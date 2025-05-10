package an.inhaintegration.icross.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class QuizResponseDto {

    private String question;
    private String answer;
    private List<String> choices;

    public QuizResponseDto(String question, String answer) {
        this.question = question;
        this.answer = answer;
    }
}
