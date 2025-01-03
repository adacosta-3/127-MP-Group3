package mp.group3.cafe.backend.DTO.Receipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptCustomizationDTO {
    private String customizationName;
    private double additionalCost;
}

