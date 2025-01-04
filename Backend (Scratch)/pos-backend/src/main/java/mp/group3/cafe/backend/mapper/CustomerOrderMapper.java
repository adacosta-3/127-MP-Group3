package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.CustomerOrderDTO;
import mp.group3.cafe.backend.entities.Customer;
import mp.group3.cafe.backend.entities.CustomerOrder;
import mp.group3.cafe.backend.entities.User;

import java.sql.Date;
import java.util.stream.Collectors;

public class CustomerOrderMapper {
    public static CustomerOrderDTO mapToCustomerOrderDTO(CustomerOrder order) {
        return new CustomerOrderDTO(
                order.getOrderId(),
                order.getOrderDate(),
                order.getCustomer() != null ? order.getCustomer().getCustomerId() : null,
                order.getCashier() != null ? order.getCashier().getUserId() : null,
                order.getTotalPrice(),
                order.getOrderLines().stream()
                        .map(OrderLineMapper::mapToOrderLineDTO)
                        .collect(Collectors.toList())
        );
    }


    public static CustomerOrder mapToCustomerOrder(CustomerOrderDTO customerOrderDTO, Customer customer, User cashier) {
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setOrderId(customerOrderDTO.getOrderId());
        customerOrder.setOrderDate(Date.valueOf(String.valueOf(customerOrderDTO.getOrderDate())));
        customerOrder.setCustomer(customer);
        customerOrder.setCashier(cashier);
        customerOrder.setTotalPrice(customerOrderDTO.getTotalPrice());
        return customerOrder;
    }
}

