package mp.group3.cafe.backend.controllers;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.OrderLineCustomizationDTO;
import mp.group3.cafe.backend.service.OrderLineCustomizationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order-line-customizations")
@RequiredArgsConstructor
public class OrderLineCustomizationController {

    private final OrderLineCustomizationService customizationService;

    @GetMapping("/order-line/{orderLineId}")
    public ResponseEntity<List<OrderLineCustomizationDTO>> getCustomizationsByOrderLineId(@PathVariable Integer orderLineId) {
        return ResponseEntity.ok(customizationService.getCustomizationsByOrderLineId(orderLineId));
    }

    @PostMapping
    public ResponseEntity<OrderLineCustomizationDTO> addCustomizationToOrderLine(@RequestBody OrderLineCustomizationDTO customizationDTO) {
        return ResponseEntity.ok(customizationService.addCustomizationToOrderLine(customizationDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCustomizationFromOrderLine(@PathVariable Integer id) {
        customizationService.removeCustomizationFromOrderLine(id);
        return ResponseEntity.noContent().build();
    }
}

