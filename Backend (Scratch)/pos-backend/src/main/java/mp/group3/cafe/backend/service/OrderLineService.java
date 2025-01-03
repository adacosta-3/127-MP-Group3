package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.OrderLineDTO;

import java.util.List;

public interface OrderLineService {
    List<OrderLineDTO> getOrderLinesByOrderId(Integer orderId);

    OrderLineDTO createOrderLine(OrderLineDTO orderLineDTO);

    OrderLineDTO updateOrderLine(Integer orderLineId, OrderLineDTO orderLineDTO);

    void deleteOrderLine(Integer orderLineId);
    OrderLineDTO addOrUpdateOrderLine(Integer orderId, OrderLineDTO orderLineDTO);
}

