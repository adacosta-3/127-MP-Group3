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
        CustomerOrder order = orderRepository.findById(orderLineDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderLineDTO.getOrderId()));

        Item item = itemRepository.findById(orderLineDTO.getItemCode())
                .orElseThrow(() -> new RuntimeException("Item not found with code: " + orderLineDTO.getItemCode()));

        ItemSize size = null;
        if (orderLineDTO.getSizeId() != null) {
            size = itemSizeRepository.findById(orderLineDTO.getSizeId())
                    .orElseThrow(() -> new RuntimeException("Size not found with ID: " + orderLineDTO.getSizeId()));
        }

        double sizeAdjustment = size != null ? size.getPriceAdjustment() : 0;
        double linePrice = (item.getBasePrice() + sizeAdjustment) * orderLineDTO.getQuantity();

        OrderLine orderLine = OrderLineMapper.mapToOrderLine(orderLineDTO, order, item);
        orderLine.setLinePrice(linePrice);

        // Save the order line to generate an ID
        OrderLine savedOrderLine = orderLineRepository.save(orderLine);

        // Process customizations (if any)
        if (orderLineDTO.getCustomizations() != null && !orderLineDTO.getCustomizations().isEmpty()) {
            for (OrderLineCustomizationDTO custDTO : orderLineDTO.getCustomizations()) {
                CustomizationOptions option = customizationOptionsRepository.findById(custDTO.getOptionId())
                        .orElseThrow(() -> new RuntimeException("Customization option not found with ID: " + custDTO.getOptionId()));
                OrderLineCustomization customization = new OrderLineCustomization();
                customization.setCustomizationOption(option);
                savedOrderLine.addCustomization(customization);
            }
        }

        // Save the customizations with updated relationships
        orderLineRepository.save(savedOrderLine);

        // Update the order total price
        updateOrderTotalPrice(order.getOrderId());

        return OrderLineMapper.mapToOrderLineDTO(savedOrderLine);
    }

    @Override
    public OrderLineDTO updateOrderLine(Integer orderLineId, OrderLineDTO orderLineDTO) {
        OrderLine existingOrderLine = orderLineRepository.findById(orderLineId)
                .orElseThrow(() -> new RuntimeException("Order line not found with ID: " + orderLineId));

        CustomerOrder order = existingOrderLine.getOrder();

        Item item = itemRepository.findById(orderLineDTO.getItemCode())
                .orElseThrow(() -> new RuntimeException("Item not found with code: " + orderLineDTO.getItemCode()));

        ItemSize size = null;
        if (orderLineDTO.getSizeId() != null) {
            size = itemSizeRepository.findById(orderLineDTO.getSizeId())
                    .orElseThrow(() -> new RuntimeException("Size not found with ID: " + orderLineDTO.getSizeId()));
        }

        double sizeAdjustment = size != null ? size.getPriceAdjustment() : 0;
        double linePrice = (item.getBasePrice() + sizeAdjustment) * orderLineDTO.getQuantity();

        existingOrderLine.setItem(item);
        existingOrderLine.setSizeId(orderLineDTO.getSizeId());
        existingOrderLine.setQuantity(orderLineDTO.getQuantity());
        existingOrderLine.setLinePrice(linePrice);

        // Update customizations
        List<OrderLineCustomization> existingCustomizations = existingOrderLine.getCustomizations();
        existingCustomizations.clear(); // Clear the existing customizations

        if (orderLineDTO.getCustomizations() != null && !orderLineDTO.getCustomizations().isEmpty()) {
            for (OrderLineCustomizationDTO custDTO : orderLineDTO.getCustomizations()) {
                CustomizationOptions option = customizationOptionsRepository.findById(custDTO.getOptionId())
                        .orElseThrow(() -> new RuntimeException("Customization option not found with ID: " + custDTO.getOptionId()));
                OrderLineCustomization customization = new OrderLineCustomization();
                customization.setCustomizationOption(option);
                existingOrderLine.addCustomization(customization);
            }
        }

        // Save the updated order line
        OrderLine updatedOrderLine = orderLineRepository.save(existingOrderLine);

        // Update the order total price
        updateOrderTotalPrice(order.getOrderId());

        return OrderLineMapper.mapToOrderLineDTO(updatedOrderLine);
    }

    public void updateOrderTotalPrice(Integer orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Fetch all order lines associated with the order
        List<OrderLine> orderLines = orderLineRepository.findByOrder_OrderId(orderId);

        // Calculate the total price by summing up line prices and customization costs
        double totalPrice = orderLines.stream()
                .mapToDouble(orderLine -> {
                    // Calculate the total customization cost for each order line
                    double customizationCost = orderLine.getCustomizations().stream()
                            .mapToDouble(c -> c.getCustomizationOption().getAdditionalCost())
                            .sum();
                    // Add the line price and customization costs
                    return orderLine.getLinePrice() + customizationCost;
                })
                .sum();

        // Update the order's total price
        order.setTotalPrice(totalPrice);
        orderRepository.save(order); // Persist the updated total price
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
