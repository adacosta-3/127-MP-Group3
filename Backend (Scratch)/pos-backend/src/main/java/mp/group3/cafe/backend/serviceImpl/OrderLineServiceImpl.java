package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
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
        CustomerOrder order = orderRepository.findById(orderLineDTO.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderLineDTO.getOrderId()));

        // Fetch the item by itemCode
        Item item = itemRepository.findById(orderLineDTO.getItemCode())
                .orElseThrow(() -> new RuntimeException("Item not found with code: " + orderLineDTO.getItemCode()));

        // Fetch the size by ID (nullable)
        ItemSize size = null;
        if (orderLineDTO.getSizeId() != null) {
            size = itemSizeRepository.findById(orderLineDTO.getSizeId())
                    .orElseThrow(() -> new RuntimeException("Size not found with ID: " + orderLineDTO.getSizeId()));
        }

        // Calculate base price
        double sizeAdjustment = size != null ? size.getPriceAdjustment() : 0;
        double basePrice = item.getBasePrice() + sizeAdjustment;

        // Fetch customizations and calculate their total price
        double customizationsPrice = orderLineDTO.getCustomizations().stream()
                .mapToDouble(customizationDTO -> {
                    CustomizationOptions customizationOption = customizationOptionsRepository
                            .findById(customizationDTO.getOptionId())
                            .orElseThrow(() -> new RuntimeException("Customization not found with ID: " + customizationDTO.getOptionId()));
                    return customizationOption.getAdditionalCost();
                })
                .sum();

        // Calculate total line price
        double linePrice = (basePrice + customizationsPrice) * orderLineDTO.getQuantity();

        // Map and save the order line
        OrderLine orderLine = OrderLineMapper.mapToOrderLine(orderLineDTO, order, item);
        orderLine.setLinePrice(linePrice);

        // Save customizations for the order line
        List<OrderLineCustomization> orderLineCustomizations = orderLineDTO.getCustomizations().stream()
                .map(customizationDTO -> {
                    CustomizationOptions customizationOption = customizationOptionsRepository
                            .findById(customizationDTO.getOptionId())
                            .orElseThrow(() -> new RuntimeException("Customization not found with ID: " + customizationDTO.getOptionId()));
                    OrderLineCustomization olc = new OrderLineCustomization();
                    olc.setOrderLine(orderLine);
                    olc.setCustomizationOption(customizationOption);
                    return olc;
                }).collect(Collectors.toList());
        orderLine.setCustomizations(orderLineCustomizations);

        OrderLine savedOrderLine = orderLineRepository.save(orderLine);

        // Update the total price of the order
        double updatedTotalPrice = order.getOrderLines().stream()
                .mapToDouble(OrderLine::getLinePrice)
                .sum();
        order.setTotalPrice(updatedTotalPrice);
        orderRepository.save(order);

        return OrderLineMapper.mapToOrderLineDTO(savedOrderLine);
    }

    @Override
    public OrderLineDTO updateOrderLine(Integer orderLineId, OrderLineDTO orderLineDTO) {
        OrderLine existingOrderLine = orderLineRepository.findById(orderLineId)
                .orElseThrow(() -> new RuntimeException("Order line not found with ID: " + orderLineId));

        CustomerOrder order = existingOrderLine.getOrder();

        // Fetch the item by itemCode
        Item item = itemRepository.findById(orderLineDTO.getItemCode())
                .orElseThrow(() -> new RuntimeException("Item not found with code: " + orderLineDTO.getItemCode()));

        // Fetch the size by ID (nullable)
        ItemSize size = null;
        if (orderLineDTO.getSizeId() != null) {
            size = itemSizeRepository.findById(orderLineDTO.getSizeId())
                    .orElseThrow(() -> new RuntimeException("Size not found with ID: " + orderLineDTO.getSizeId()));
        }

        // Calculate base price
        double sizeAdjustment = size != null ? size.getPriceAdjustment() : 0;
        double basePrice = item.getBasePrice() + sizeAdjustment;

        // Fetch customizations and calculate their total price
        double customizationsPrice = orderLineDTO.getCustomizations().stream()
                .mapToDouble(customizationDTO -> {
                    CustomizationOptions customizationOption = customizationOptionsRepository
                            .findById(customizationDTO.getOptionId())
                            .orElseThrow(() -> new RuntimeException("Customization not found with ID: " + customizationDTO.getOptionId()));
                    return customizationOption.getAdditionalCost();
                })
                .sum();

        // Calculate total line price
        double linePrice = (basePrice + customizationsPrice) * orderLineDTO.getQuantity();

        // Update the existing order line
        existingOrderLine.setItem(item);
        existingOrderLine.setSizeId(orderLineDTO.getSizeId());
        existingOrderLine.setQuantity(orderLineDTO.getQuantity());
        existingOrderLine.setLinePrice(linePrice);

        // Update customizations
        List<OrderLineCustomization> updatedCustomizations = orderLineDTO.getCustomizations().stream()
                .map(customizationDTO -> {
                    CustomizationOptions customizationOption = customizationOptionsRepository
                            .findById(customizationDTO.getOptionId())
                            .orElseThrow(() -> new RuntimeException("Customization not found with ID: " + customizationDTO.getOptionId()));
                    OrderLineCustomization olc = new OrderLineCustomization();
                    olc.setOrderLine(existingOrderLine);
                    olc.setCustomizationOption(customizationOption);
                    return olc;
                }).collect(Collectors.toList());
        existingOrderLine.setCustomizations(updatedCustomizations);

        OrderLine updatedOrderLine = orderLineRepository.save(existingOrderLine);

        // Update the total price of the order
        double updatedTotalPrice = order.getOrderLines().stream()
                .mapToDouble(OrderLine::getLinePrice)
                .sum();
        order.setTotalPrice(updatedTotalPrice);
        orderRepository.save(order);

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






}
