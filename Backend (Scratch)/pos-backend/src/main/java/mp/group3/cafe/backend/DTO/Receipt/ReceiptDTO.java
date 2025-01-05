package mp.group3.cafe.backend.DTO.Receipt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReceiptDTO {
    private Integer orderId;
    private Date orderDate;
    private List<ReceiptItemDTO> items;
    private double totalPrice;
}

