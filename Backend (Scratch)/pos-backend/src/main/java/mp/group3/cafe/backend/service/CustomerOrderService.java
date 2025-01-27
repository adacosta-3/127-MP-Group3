package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryForMemberDTO;
import mp.group3.cafe.backend.DTO.CustomerOrderDTO;
import mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryPerDayDTO;
import mp.group3.cafe.backend.DTO.Receipt.ReceiptDTO;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface CustomerOrderService {
    List<CustomerOrderDTO> getAllOrders();

    Optional<CustomerOrderDTO> getOrderById(Integer orderId);

    CustomerOrderDTO createOrder(CustomerOrderDTO orderDTO);
    CustomerOrderDTO updateOrder(Integer orderId, CustomerOrderDTO orderDTO);


    void deleteOrder(Integer orderId);

    List<CustomerOrderDTO> getOrdersByCustomerId(Integer customerId);

    void updateOrderTotalPrice(Integer orderId);
    ReceiptDTO completeTransaction(Integer orderId);
    List<OrderHistoryPerDayDTO> getOrderHistoryPerDay();
    List<OrderHistoryForMemberDTO> getOrderHistoryForMember(String memberId);


    List<CustomerOrderDTO> getOrdersByDate(LocalDate date);



}

