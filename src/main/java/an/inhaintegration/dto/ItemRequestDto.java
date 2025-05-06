package an.inhaintegration.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    private String name;
    private int allStockQuantity;
    private int ingStockQuantity;
    private String category;
}