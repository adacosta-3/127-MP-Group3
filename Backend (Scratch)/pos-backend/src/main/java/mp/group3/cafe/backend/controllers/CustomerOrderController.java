package mp.group3.cafe.backend.controllers;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CustomerOrderDTO;
import mp.group3.cafe.backend.service.CustomerOrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/customer-orders")
@RequiredArgsConstructor
public class CustomerOrderController {

    private final CustomerOrderService customerOrderService;

    @GetMapping
    public ResponseEntity<List<CustomerOrderDTO>> getAllOrders() {
        List<CustomerOrderDTO> orders = customerOrderService.getAllOrders();
        orders.forEach(order -> customerOrderService.updateOrderTotalPrice(order.getOrderId()));
        return ResponseEntity.ok(orders);
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerOrderDTO> getOrderById(@PathVariable Integer id) {
        Optional<CustomerOrderDTO> orderOpt = customerOrderService.getOrderById(id);
        return orderOpt.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<CustomerOrderDTO>> getOrdersByCustomerId(@PathVariable Integer customerId) {
        List<CustomerOrderDTO> orders = customerOrderService.getOrdersByCustomerId(customerId);
        return ResponseEntity.ok(orders);
    }

    @PostMapping
    public ResponseEntity<CustomerOrderDTO> createOrder(@RequestBody CustomerOrderDTO orderDTO) {
        try {
            CustomerOrderDTO createdOrder = customerOrderService.createOrder(orderDTO);
            return ResponseEntity.ok(createdOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<CustomerOrderDTO> updateOrder(@PathVariable Integer id, @RequestBody CustomerOrderDTO orderDTO) {
        try {
            CustomerOrderDTO updatedOrder = customerOrderService.updateOrder(id, orderDTO);
            return ResponseEntity.ok(updatedOrder);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Integer id) {
        customerOrderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}

