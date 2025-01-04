package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.AdminDashboard.ItemOrderStatsDTO;
import mp.group3.cafe.backend.DTO.OrderLineCustomizationDTO;
import mp.group3.cafe.backend.DTO.OrderLineDTO;
import mp.group3.cafe.backend.entities.*;
import mp.group3.cafe.backend.mapper.OrderLineMapper;
import mp.group3.cafe.backend.repositories.*;
import mp.group3.cafe.backend.service.OrderLineService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderLineServiceImpl implements OrderLineService {

    private final OrderLineRepository orderLineRepository;
    private final CustomizationOptionsRepository customizationOptionsRepository;
    private final CustomerOrderRepository orderRepository;
    private final ItemRepository itemRepository;
    private final ItemSizeRepository itemSizeRepository;

    @Override
    public List<OrderLineDTO> getOrderLinesByOrderId(Integer orderId) {
        return orderLineRepository.findByOrder_OrderId(orderId)
                .stream()
                .map(OrderLineMapper::mapToOrderLineDTO)
                .collect(Collectors.toList());
    }

    @Override
    public OrderLineDTO createOrderLine(OrderLineDTO orderLineDTO) {
        // Fetch the order by ID
        Optional<CustomerOrder> orderOpt = orderRepository.findById(orderLineDTO.getOrderId());
        if (orderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderLineDTO.getOrderId());
        }

        // Fetch the size by ID
        Optional<ItemSize> sizeOpt = itemSizeRepository.findById(orderLineDTO.getSizeId());
        if (sizeOpt.isEmpty()) {
            throw new RuntimeException("Size not found with ID: " + orderLineDTO.getSizeId());
        }

        // Fetch the item by itemCode (primary key)
        Optional<Item> itemOpt = itemRepository.findById(orderLineDTO.getItemCode());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with code: " + orderLineDTO.getItemCode());
        }

        Item item = itemOpt.get();
        ItemSize size = sizeOpt.get();

        // Calculate line price
        double linePrice = (item.getBasePrice() + size.getPriceAdjustment()) * orderLineDTO.getQuantity();

        // Create and save the order line
        OrderLine orderLine = new OrderLine();
        orderLine.setOrder(orderOpt.get());
        orderLine.setItem(item);
        orderLine.setSizeId(size.getSizeId());
        orderLine.setQuantity(orderLineDTO.getQuantity());
        orderLine.setLinePrice(linePrice);

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

        Optional<Item> itemOpt = itemRepository.findById(orderLineDTO.getItemCode());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + orderLineDTO.getItemCode());
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

    @Override
    public OrderLineDTO addOrUpdateOrderLine(Integer orderId, OrderLineDTO orderLineDTO) {
        // Fetch the order
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Fetch the item and size
        Item item = itemRepository.findById(orderLineDTO.getItemCode())
                .orElseThrow(() -> new RuntimeException("Item not found"));

        ItemSize size = itemSizeRepository.findById(orderLineDTO.getSizeId())
                .orElseThrow(() -> new RuntimeException("Size not found"));

        // Fetch customizations
        List<CustomizationOptions> selectedOptions = customizationOptionsRepository.findAllById(
                orderLineDTO.getCustomizations().stream()
                        .map(OrderLineCustomizationDTO::getOptionId)
                        .collect(Collectors.toList())
        );

        // Calculate the base price
        double basePrice = item.getBasePrice() + size.getPriceAdjustment();

        // Add customization costs
        double customizationCost = selectedOptions.stream()
                .mapToDouble(CustomizationOptions::getAdditionalCost)
                .sum();

        // Calculate the final line price
        double linePrice = (basePrice + customizationCost) * orderLineDTO.getQuantity();

        // Check for duplicate order lines
        Optional<OrderLine> existingOrderLine = orderLineRepository.findDuplicateOrderLine(
                orderId,
                orderLineDTO.getItemCode(),
                orderLineDTO.getSizeId(),
                selectedOptions.stream()
                        .map(CustomizationOptions::getOptionId)
                        .collect(Collectors.toList()),
                (long) selectedOptions.size()
        );

        if (existingOrderLine.isPresent()) {
            // Update quantity and price for the existing line
            OrderLine line = existingOrderLine.get();
            line.setQuantity(line.getQuantity() + orderLineDTO.getQuantity());
            line.setLinePrice(line.getLinePrice() + linePrice);
            orderLineRepository.save(line);
            return OrderLineMapper.mapToOrderLineDTO(line);
        }

        // Create a new order line
        OrderLine orderLine = new OrderLine();
        orderLine.setOrder(order);
        orderLine.setItem(item);
        orderLine.setSizeId(orderLineDTO.getSizeId());
        orderLine.setQuantity(orderLineDTO.getQuantity());
        orderLine.setLinePrice(linePrice);

        // Save customizations for the order line
        List<OrderLineCustomization> orderLineCustomizations = selectedOptions.stream()
                .map(option -> {
                    OrderLineCustomization olc = new OrderLineCustomization();
                    olc.setOrderLine(orderLine);
                    olc.setCustomizationOption(option);
                    return olc;
                }).collect(Collectors.toList());
        orderLine.setCustomizations(orderLineCustomizations);

        orderLineRepository.save(orderLine);
        return OrderLineMapper.mapToOrderLineDTO(orderLine);
    }

    @Override
    public List<ItemOrderStatsDTO> getMostOrderedItems() {
        return orderLineRepository.findMostOrderedItems();
    }

    @Override
    public List<ItemOrderStatsDTO> getLeastOrderedItems() {
        return orderLineRepository.findLeastOrderedItems();
    }


}
