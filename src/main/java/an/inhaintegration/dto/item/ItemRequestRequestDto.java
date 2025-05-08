package an.inhaintegration.dto.item;

import lombok.Data;

@Data
public class ItemRequestRequestDto {

    private Long studentId;
    private Long itemId;
    private String content;
}
