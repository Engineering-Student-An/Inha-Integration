package an.inhaintegration.rentalee.dto.item;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    private String name;
    private int allStockQuantity;
    private int ingStockQuantity;
    private String category;
}