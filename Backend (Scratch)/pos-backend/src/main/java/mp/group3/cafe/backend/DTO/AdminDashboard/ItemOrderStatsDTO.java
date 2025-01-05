package mp.group3.cafe.backend.DTO.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemOrderStatsDTO {
    private String itemCode;
    private String itemName;
    private Long totalQuantity;
}


