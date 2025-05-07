package an.inhaintegration.dto.item;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ItemResponseDto {

    private Long itemId;
    private String name;
    private int stockQuantity;
    private int allStockQuantity;

}
