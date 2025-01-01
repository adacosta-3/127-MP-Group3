package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.CustomerDTO;
import mp.group3.cafe.backend.entities.Customer;

public class CustomerMapper {
    public static CustomerDTO mapToCustomerDTO(Customer customer) {
        return new CustomerDTO(
                customer.getCustomerId(),
                customer.getType(),
                customer.getFirstName(),
                customer.getLastName()
        );
    }

    public static Customer mapToCustomer(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        customer.setCustomerId(customerDTO.getCustomerId());
        customer.setType(customerDTO.getType());
        customer.setFirstName(customerDTO.getFirstName());
        customer.setLastName(customerDTO.getLastName());
        return customer;
    }
}
