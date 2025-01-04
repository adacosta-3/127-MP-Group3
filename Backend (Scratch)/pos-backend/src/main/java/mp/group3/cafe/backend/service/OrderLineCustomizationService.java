package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.OrderLineCustomizationDTO;

import java.util.List;

public interface OrderLineCustomizationService {
    List<OrderLineCustomizationDTO> getCustomizationsByOrderLineId(Integer orderLineId);

    OrderLineCustomizationDTO addCustomizationToOrderLine(OrderLineCustomizationDTO customizationDTO);

    void removeCustomizationFromOrderLine(Integer customizationId);
}
