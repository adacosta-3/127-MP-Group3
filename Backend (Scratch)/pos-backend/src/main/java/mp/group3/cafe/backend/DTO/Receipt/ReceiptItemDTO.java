package mp.group3.cafe.backend.DTO.Receipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptItemDTO {
    private String itemName;
    private int quantity;
    private double linePrice; // Includes customizations and quantity
    private List<ReceiptCustomizationDTO> customizations;
}

