package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomerOrderDTO {
    private Integer orderId;
    private String orderDate;
    private Integer customerId; // Nullable for guests
    private Integer cashierId;
    private Double totalPrice;
}
