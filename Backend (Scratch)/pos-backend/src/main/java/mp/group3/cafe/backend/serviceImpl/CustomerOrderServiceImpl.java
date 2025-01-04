package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryForMemberDTO;
import mp.group3.cafe.backend.DTO.CustomerOrderDTO;
import mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryPerDayDTO;
import mp.group3.cafe.backend.DTO.Receipt.ReceiptCustomizationDTO;
import mp.group3.cafe.backend.DTO.Receipt.ReceiptDTO;
import mp.group3.cafe.backend.DTO.Receipt.ReceiptItemDTO;
import mp.group3.cafe.backend.entities.*;
import mp.group3.cafe.backend.mapper.CustomerOrderMapper;
import mp.group3.cafe.backend.repositories.*;
import mp.group3.cafe.backend.service.CustomerOrderService;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerOrderServiceImpl implements CustomerOrderService {

    private final CustomerOrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final OrderLineRepository orderLineRepository;
    private final OrderLineCustomizationRepository orderLineCustomizationRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomerOrderServiceImpl.class);

    @Override
    public List<CustomerOrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(CustomerOrderMapper::mapToCustomerOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerOrderDTO> getOrderById(Integer orderId) {
        Optional<CustomerOrder> orderOpt = orderRepository.findById(orderId);

        // Recalculate total price if order exists
        orderOpt.ifPresent(order -> updateOrderTotalPrice(order.getOrderId()));

        return orderOpt.map(CustomerOrderMapper::mapToCustomerOrderDTO);
    }


    @Override
    public CustomerOrderDTO createOrder(CustomerOrderDTO orderDTO) {
        logger.info("Starting to create order with payload: {}", orderDTO);

        Optional<Customer> customerOpt = Optional.empty();
        if (orderDTO.getCustomerId() != null) {
            customerOpt = customerRepository.findById(orderDTO.getCustomerId());
            if (customerOpt.isEmpty()) {
                throw new RuntimeException("Customer not found with ID: " + orderDTO.getCustomerId());
            }
        }

        Optional<User> cashierOpt = userRepository.findById(orderDTO.getCashierId());
        if (cashierOpt.isEmpty()) {
            throw new RuntimeException("Cashier not found with ID: " + orderDTO.getCashierId());
        }

        CustomerOrder order = new CustomerOrder();
        java.sql.Date sqlDate = new java.sql.Date(orderDTO.getOrderDate().getTime());
        order.setOrderDate(sqlDate);
        order.setCustomer(customerOpt.orElse(null)); // Null for guest customers
        order.setCashier(cashierOpt.get());
        order.setTotalPrice(0.0); // Temporary value, will update after OrderLines are added

        CustomerOrder savedOrder = orderRepository.save(order);
        updateOrderTotalPrice(savedOrder.getOrderId());

        return CustomerOrderMapper.mapToCustomerOrderDTO(savedOrder);
    }

    @Override
    public CustomerOrderDTO updateOrder(Integer orderId, CustomerOrderDTO orderDTO) {
        Optional<CustomerOrder> existingOrderOpt = orderRepository.findById(orderId);
        if (existingOrderOpt.isEmpty()) {
            throw new RuntimeException("Order not found with ID: " + orderId);
        }

        CustomerOrder existingOrder = existingOrderOpt.get();

        if (orderDTO.getCustomerId() != null) {
            Optional<Customer> customerOpt = customerRepository.findById(orderDTO.getCustomerId());
            if (customerOpt.isEmpty()) {
                throw new RuntimeException("Customer not found with ID: " + orderDTO.getCustomerId());
            }
            existingOrder.setCustomer(customerOpt.get());
        }

        if (orderDTO.getCashierId() != null) {
            Optional<User> cashierOpt = userRepository.findById(orderDTO.getCashierId());
            if (cashierOpt.isEmpty()) {
                throw new RuntimeException("Cashier not found with ID: " + orderDTO.getCashierId());
            }
            existingOrder.setCashier(cashierOpt.get());
        }

        existingOrder.setOrderDate(java.sql.Date.valueOf(String.valueOf(orderDTO.getOrderDate())));

        CustomerOrder updatedOrder = orderRepository.save(existingOrder);

        // Recalculate total price
        updateOrderTotalPrice(updatedOrder.getOrderId());

        return CustomerOrderMapper.mapToCustomerOrderDTO(updatedOrder);
    }

    public void updateOrderTotalPrice(Integer orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        List<OrderLine> orderLines = orderLineRepository.findByOrder_OrderId(orderId);

        double totalPrice = orderLines.stream()
                .mapToDouble(orderLine -> {
                    double customizationCost = orderLine.getCustomizations().stream()
                            .mapToDouble(c -> c.getCustomizationOption().getAdditionalCost())
                            .sum();
                    return orderLine.getLinePrice() + customizationCost;
                })
                .sum();

        // Update the order's total price
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
    }

    @Override
    public ReceiptDTO completeTransaction(Integer orderId) {
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Fetch all order lines for the order
        List<OrderLine> orderLines = orderLineRepository.findByOrder_OrderId(orderId);

        // Aggregate receipt details
        List<ReceiptItemDTO> receiptItems = orderLines.stream().map(orderLine -> {
            // Fetch customizations for each order line
            List<OrderLineCustomization> customizations = orderLine.getCustomizations();

            // Map customizations to DTOs
            List<ReceiptCustomizationDTO> customizationDTOs = customizations.stream()
                    .map(c -> new ReceiptCustomizationDTO(
                            c.getCustomizationOption().getOptionName(),
                            c.getCustomizationOption().getAdditionalCost()
                    ))
                    .collect(Collectors.toList());

            // Calculate price per item
            double itemPrice = orderLine.getLinePrice();

            // Return the ReceiptItemDTO
            return new ReceiptItemDTO(
                    orderLine.getItem().getName(),
                    orderLine.getQuantity(),
                    itemPrice,
                    customizationDTOs
            );
        }).collect(Collectors.toList());

        // Calculate total price
        double totalPrice = order.getTotalPrice();

        // Return the receipt
        return new ReceiptDTO(orderId, order.getOrderDate(), receiptItems, totalPrice);
    }

    @Override
    public List<OrderHistoryPerDayDTO> getOrderHistoryPerDay() {
        return orderRepository.findOrderHistoryPerDay();
    }

    @Override
    public List<OrderHistoryForMemberDTO> getOrderHistoryForMember(String memberId) {
        return orderRepository.findOrderHistoryForMember(memberId);
    }


    @Override
    public void deleteOrder(Integer orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<CustomerOrderDTO> getOrdersByCustomerId(Integer customerId) {
        List<CustomerOrder> orders = orderRepository.findByCustomer_CustomerId(customerId);

        // Recalculate total price for each order
        orders.forEach(order -> updateOrderTotalPrice(order.getOrderId()));

        return orders.stream()
                .map(CustomerOrderMapper::mapToCustomerOrderDTO)
                .collect(Collectors.toList());
    }

}

