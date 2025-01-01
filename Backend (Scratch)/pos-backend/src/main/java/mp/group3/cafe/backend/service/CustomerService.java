package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.CustomerDTO;

import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();

    Optional<CustomerDTO> getCustomerById(Integer customerId);

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomer(Integer customerId, CustomerDTO customerDTO);

    void deleteCustomer(Integer customerId);
}

