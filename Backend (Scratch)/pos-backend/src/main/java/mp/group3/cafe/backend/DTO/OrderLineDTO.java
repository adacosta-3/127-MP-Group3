package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class OrderLineDTO {
    private Integer orderLineId;
    private Integer orderId;
    private String itemCode;
    private Integer sizeId; // Nullable for non-sized items
    private Integer quantity;
    private Double linePrice;
    private List<OrderLineCustomizationDTO> customizations;
}


