package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.CustomerOrderDTO;
import mp.group3.cafe.backend.entities.Customer;
import mp.group3.cafe.backend.entities.CustomerOrder;
import mp.group3.cafe.backend.entities.User;

import java.sql.Date;

public class CustomerOrderMapper {
    public static CustomerOrderDTO mapToCustomerOrderDTO(CustomerOrder customerOrder) {
        return new CustomerOrderDTO(
                customerOrder.getOrderId(),
                customerOrder.getOrderDate().toString(),
                customerOrder.getCustomer() != null ? customerOrder.getCustomer().getCustomerId() : null,
                customerOrder.getCashier().getUserId(),
                customerOrder.getTotalPrice()
        );
    }

    public static CustomerOrder mapToCustomerOrder(CustomerOrderDTO customerOrderDTO, Customer customer, User cashier) {
        CustomerOrder customerOrder = new CustomerOrder();
        customerOrder.setOrderId(customerOrderDTO.getOrderId());
        customerOrder.setOrderDate(Date.valueOf(customerOrderDTO.getOrderDate()));
        customerOrder.setCustomer(customer);
        customerOrder.setCashier(cashier);
        customerOrder.setTotalPrice(customerOrderDTO.getTotalPrice());
        return customerOrder;
    }
}

