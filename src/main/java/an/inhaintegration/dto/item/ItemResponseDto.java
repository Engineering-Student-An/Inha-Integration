package an.inhaintegration.dto.item;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemResponseDto {

    private Long itemId;
    private String name;
    private int stockQuantity;
    private int allStockQuantity;
}
