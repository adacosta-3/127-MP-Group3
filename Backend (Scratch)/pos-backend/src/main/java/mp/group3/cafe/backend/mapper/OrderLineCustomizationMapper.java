package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.OrderLineCustomizationDTO;
import mp.group3.cafe.backend.entities.CustomizationOptions;
import mp.group3.cafe.backend.entities.OrderLine;
import mp.group3.cafe.backend.entities.OrderLineCustomization;

public class OrderLineCustomizationMapper {
    public static OrderLineCustomizationDTO mapToOrderLineCustomizationDTO(OrderLineCustomization orderLineCustomization) {
        return new OrderLineCustomizationDTO(
                orderLineCustomization.getLineCustomizationId(),
                orderLineCustomization.getOrderLine().getOrderLineId(),
                orderLineCustomization.getCustomizationOption().getOptionId()
        );
    }

    public static OrderLineCustomization mapToOrderLineCustomization(OrderLineCustomizationDTO orderLineCustomizationDTO, OrderLine orderLine, CustomizationOptions customizationOptions) {
        OrderLineCustomization orderLineCustomization = new OrderLineCustomization();
        orderLineCustomization.setLineCustomizationId(orderLineCustomizationDTO.getLineCustomizationId());
        orderLineCustomization.setOrderLine(orderLine);
        orderLineCustomization.setCustomizationOption(customizationOptions);
        return orderLineCustomization;
    }
}
