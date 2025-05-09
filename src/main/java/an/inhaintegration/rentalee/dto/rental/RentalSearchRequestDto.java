package an.inhaintegration.rentalee.dto.rental;

import an.inhaintegration.rentalee.domain.RentalStatus;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class RentalSearchRequestDto {

    private String stuId;
    private RentalStatus rentalStatus;  // ING, FINISH, OVERDUE
    private String itemName;
}
