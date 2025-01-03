package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CustomerOrderDTO;
import mp.group3.cafe.backend.entities.*;
import mp.group3.cafe.backend.mapper.CustomerOrderMapper;
import mp.group3.cafe.backend.repositories.*;
import mp.group3.cafe.backend.service.CustomerOrderService;
import org.springframework.stereotype.Service;

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

    @Override
    public List<CustomerOrderDTO> getAllOrders() {
        return orderRepository.findAll()
                .stream()
                .map(CustomerOrderMapper::mapToCustomerOrderDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerOrderDTO> getOrderById(Integer orderId) {
        return orderRepository.findById(orderId)
                .map(CustomerOrderMapper::mapToCustomerOrderDTO);
    }

    @Override
    public CustomerOrderDTO createOrder(CustomerOrderDTO orderDTO) {
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
        order.setOrderDate(java.sql.Date.valueOf(String.valueOf(orderDTO.getOrderDate())));
        order.setCustomer(customerOpt.orElse(null)); // Null for guest customers
        order.setCashier(cashierOpt.get());
        order.setTotalPrice(0.0); // Temporary value, will update after OrderLines are added

        CustomerOrder savedOrder = orderRepository.save(order);

        // Calculate total price after OrderLines are added
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
        // Fetch the order
        CustomerOrder order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found with ID: " + orderId));

        // Fetch all order lines for this order
        List<OrderLine> orderLines = orderLineRepository.findByOrder_OrderId(orderId);

        // Calculate total price by summing up line prices
        double totalPrice = orderLines.stream()
                .mapToDouble(OrderLine::getLinePrice) // Use the precomputed line price
                .sum();

        // Update the order's total price
        order.setTotalPrice(totalPrice);
        orderRepository.save(order);
    }




    @Override
    public void deleteOrder(Integer orderId) {
        orderRepository.deleteById(orderId);
    }

    @Override
    public List<CustomerOrderDTO> getOrdersByCustomerId(Integer customerId) {
        return orderRepository.findByCustomer_CustomerId(customerId)
                .stream()
                .map(CustomerOrderMapper::mapToCustomerOrderDTO)
                .collect(Collectors.toList());
    }
}

