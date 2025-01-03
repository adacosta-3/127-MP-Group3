package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.OrderLineDTO;
import mp.group3.cafe.backend.entities.CustomerOrder;
import mp.group3.cafe.backend.entities.Item;
import mp.group3.cafe.backend.entities.OrderLine;

import java.util.stream.Collectors;

public class OrderLineMapper {
    public static OrderLineDTO mapToOrderLineDTO(OrderLine orderLine) {
        return new OrderLineDTO(
                orderLine.getOrderLineId(),
                orderLine.getOrder().getOrderId(),
                orderLine.getItem().getItemCode(),
                orderLine.getSizeId(),
                orderLine.getQuantity(),
                orderLine.getLinePrice(),
                orderLine.getCustomizations().stream()
                        .map(OrderLineCustomizationMapper::mapToOrderLineCustomizationDTO)
                        .collect(Collectors.toList())
        );
    }

    public static OrderLine mapToOrderLine(OrderLineDTO orderLineDTO, CustomerOrder order, Item item) {
        OrderLine orderLine = new OrderLine();
        orderLine.setOrderLineId(orderLineDTO.getOrderLineId());
        orderLine.setOrder(order);
        orderLine.setItem(item);
        orderLine.setSizeId(orderLineDTO.getSizeId());
        orderLine.setQuantity(orderLineDTO.getQuantity());
        orderLine.setLinePrice(orderLineDTO.getLinePrice());
        return orderLine;
    }
}


