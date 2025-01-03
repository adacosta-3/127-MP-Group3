package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.CustomerDTO;
import mp.group3.cafe.backend.DTO.CustomizationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CustomerService {
    List<CustomerDTO> getAllCustomers();

    Optional<CustomerDTO> getCustomerById(Integer customerId);

    CustomerDTO createCustomer(CustomerDTO customerDTO);

    CustomerDTO updateCustomer(Integer customerId, CustomerDTO customerDTO);

    void deleteCustomer(Integer customerId);
}

