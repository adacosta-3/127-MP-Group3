package mp.group3.cafe.backend.DTO.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryForMemberDTO {
    private Integer orderId;
    private LocalDate orderDate;
    private Double totalPrice;
}

