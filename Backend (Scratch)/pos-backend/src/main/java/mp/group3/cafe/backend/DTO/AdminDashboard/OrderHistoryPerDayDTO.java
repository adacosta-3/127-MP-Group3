package mp.group3.cafe.backend.DTO.AdminDashboard;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHistoryPerDayDTO {
    private LocalDate orderDate;
    private Long orderCount;
}

