package an.inhaintegration.icross.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class QuizRequestDto {

    int ox;
    int multipleChoice;
    int shortAnswer;

    private MultipartFile lectureNote;
}
