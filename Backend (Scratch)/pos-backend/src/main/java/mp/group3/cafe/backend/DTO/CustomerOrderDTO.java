package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class CustomerOrderDTO {
    private Integer orderId;
    private Date orderDate;
    private Integer customerId; // Nullable for guests
    private Integer cashierId;
    private Double totalPrice;
    private List<OrderLineDTO> orderLines;
}
