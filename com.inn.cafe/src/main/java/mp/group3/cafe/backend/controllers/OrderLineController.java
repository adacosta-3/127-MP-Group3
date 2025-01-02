package mp.group3.cafe.backend.controllers;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.OrderLineDTO;
import mp.group3.cafe.backend.service.OrderLineService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-lines")
@RequiredArgsConstructor
public class OrderLineController {

    private final OrderLineService orderLineService;

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderLineDTO>> getOrderLinesByOrderId(@PathVariable Integer orderId) {
        return ResponseEntity.ok(orderLineService.getOrderLinesByOrderId(orderId));
    }

    @PostMapping
    public ResponseEntity<OrderLineDTO> createOrderLine(@RequestBody OrderLineDTO orderLineDTO) {
        return ResponseEntity.ok(orderLineService.createOrderLine(orderLineDTO));
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderLineDTO> updateOrderLine(@PathVariable Integer id, @RequestBody OrderLineDTO orderLineDTO) {
        return ResponseEntity.ok(orderLineService.updateOrderLine(id, orderLineDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrderLine(@PathVariable Integer id) {
        orderLineService.deleteOrderLine(id);
        return ResponseEntity.noContent().build();
    }
}
