package an.inhaintegration.dto.rental;

import an.inhaintegration.domain.RentalStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RentalResponseDto {

    private Long rentalId;

    private RentalStatus status;
    private String itemName;
    private String itemCategory;
    private LocalDate rentalDate;
}