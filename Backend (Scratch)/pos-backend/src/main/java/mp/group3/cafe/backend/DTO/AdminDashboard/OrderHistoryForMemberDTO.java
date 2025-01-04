package mp.group3.cafe.backend.DTO.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryForMemberDTO {
    private Integer orderId;
    private Date orderDate;
    private Double totalPrice;
}

