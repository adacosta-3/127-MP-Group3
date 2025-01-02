package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.OrderLineDTO;
import mp.group3.cafe.backend.entities.CustomerOrder;
import mp.group3.cafe.backend.entities.Item;
import mp.group3.cafe.backend.entities.OrderLine;
import mp.group3.cafe.backend.mapper.OrderLineMapper;
import mp.group3.cafe.backend.repositories.CustomerOrderRepository;
import mp.group3.cafe.backend.repositories.ItemRepository;
import mp.group3.cafe.backend.repositories.OrderLineRepository;
import mp.group3.cafe.backend.service.OrderLineService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderLineServiceImpl implements OrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final CustomerOrderRepository orderRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<OrderLineDTO> getOrderLinesByOrderId(Integer orderId) {
        return orderLineRepository.findByOrder_OrderId(orderId)
                .stream()
                .map(OrderLineMapper::mapToOrderLineDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderLineDTO createOrderLine(OrderLineDTO orderLineDTO) {
        Optional<CustomerOrder> orderOpt = orderRepository.findById(orderLineDTO.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderLineDTO.getOrderId());
        }

        Optional<Item> itemOpt = itemRepository.findById(orderLineDTO.getItemId());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + orderLineDTO.getItemId());
        }

        // Retrieve order and item
        CustomerOrder order = orderOpt.get();
        Item item = itemOpt.get();

        // Calculate line price: item base price * quantity
        double linePrice = item.getBasePrice() * orderLineDTO.getQuantity();

        // Create and save order line
        OrderLine orderLine = OrderLineMapper.mapToOrderLine(orderLineDTO, order, item);
        orderLine.setLinePrice(linePrice); // Set calculated line price
        OrderLine savedOrderLine = orderLineRepository.save(orderLine);

        return OrderLineMapper.mapToOrderLineDTO(savedOrderLine);
    }

    @Override
    public OrderLineDTO updateOrderLine(Integer orderLineId, OrderLineDTO orderLineDTO) {
        Optional<OrderLine> existingOrderLineOpt = orderLineRepository.findById(orderLineId);
        if (existingOrderLineOpt.isEmpty()) {
            throw new RuntimeException("Order line not found with ID: " + orderLineId);
        }

        Optional<CustomerOrder> orderOpt = orderRepository.findById(orderLineDTO.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderLineDTO.getOrderId());
        }

        Optional<Item> itemOpt = itemRepository.findById(orderLineDTO.getItemId());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + orderLineDTO.getItemId());
        }

        // Retrieve existing order line, order, and item
        OrderLine existingOrderLine = existingOrderLineOpt.get();
        CustomerOrder order = orderOpt.get();
        Item item = itemOpt.get();

        // Calculate line price: item base price * quantity
        double linePrice = item.getBasePrice() * orderLineDTO.getQuantity();

        // Update order line details
        existingOrderLine.setOrder(order);
        existingOrderLine.setItem(item);
        existingOrderLine.setQuantity(orderLineDTO.getQuantity());
        existingOrderLine.setLinePrice(linePrice); // Update calculated line price

        OrderLine updatedOrderLine = orderLineRepository.save(existingOrderLine);

        return OrderLineMapper.mapToOrderLineDTO(updatedOrderLine);
    }

    @Override
    public void deleteOrderLine(Integer orderLineId) {
        orderLineRepository.deleteById(orderLineId);
    }
}
