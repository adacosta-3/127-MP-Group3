package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderLineDTO {
    private Integer orderLineId;
    private Integer orderId;
    private Integer itemId;
    private Integer sizeId; // Nullable for non-sized items
    private Integer quantity;
    private Double linePrice;
}


