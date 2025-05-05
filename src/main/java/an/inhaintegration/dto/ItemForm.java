package an.inhaintegration.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class ItemForm {

    private Long id;

    private String name;
    private int allStockQuantity;
    private int ingStockQuantity;
    private String category;

}
