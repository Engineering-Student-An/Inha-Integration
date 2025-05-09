package an.inhaintegration.rentalee.dto.item;

import an.inhaintegration.rentalee.dto.student.StudentResponseDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestResponseDto {

    private Long itemRequestId;
    private StudentResponseDto student;
    private String itemCategory;
    private String itemName;
    private String content;
    private boolean checked;
}
