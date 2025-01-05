package mp.group3.cafe.backend.controllers;


import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.AdminDashboard.ItemOrderStatsDTO;
import mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryForMemberDTO;
import mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryPerDayDTO;
import mp.group3.cafe.backend.DTO.CustomerOrderDTO;
import mp.group3.cafe.backend.entities.CustomerOrder;
import mp.group3.cafe.backend.service.CustomerOrderService;
import mp.group3.cafe.backend.service.OrderLineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.List;


@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {


    private final CustomerOrderService customerOrderService;
    private final OrderLineService orderLineService;


    @GetMapping("/order-history-per-day")
    public ResponseEntity<List<OrderHistoryPerDayDTO>> getOrderHistoryPerDay() {
        List<OrderHistoryPerDayDTO> orderHistory = customerOrderService.getOrderHistoryPerDay();
        return ResponseEntity.ok(orderHistory);
    }


    @GetMapping("/order-history/member/{memberId}")
    public ResponseEntity<List<OrderHistoryForMemberDTO>> getOrderHistoryForMember(@PathVariable String memberId) {
        List<OrderHistoryForMemberDTO> orderHistory = customerOrderService.getOrderHistoryForMember(memberId);
        return ResponseEntity.ok(orderHistory);
    }


    @GetMapping("/most-ordered-items")
    public ResponseEntity<List<ItemOrderStatsDTO>> getMostOrderedItems() {
        List<ItemOrderStatsDTO> mostOrderedItems = orderLineService.getMostOrderedItems();
        return ResponseEntity.ok(mostOrderedItems);
    }


    @GetMapping("/least-ordered-items")
    public ResponseEntity<List<ItemOrderStatsDTO>> getLeastOrderedItems() {
        List<ItemOrderStatsDTO> leastOrderedItems = orderLineService.getLeastOrderedItems();
        return ResponseEntity.ok(leastOrderedItems);
    }


    @GetMapping("/order-history-by-date/{date}")
    public ResponseEntity<List<CustomerOrderDTO>> getOrdersByDate(@PathVariable String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<CustomerOrderDTO> orders = customerOrderService.getOrdersByDate(parsedDate);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


}
