package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CustomerDTO;
import mp.group3.cafe.backend.entities.Customer;
import mp.group3.cafe.backend.mapper.CustomerMapper;
import mp.group3.cafe.backend.repositories.CustomerRepository;
import mp.group3.cafe.backend.service.CustomerService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;

    @Override
    public List<CustomerDTO> getAllCustomers() {
        return customerRepository.findAll()
                .stream()
                .map(CustomerMapper::mapToCustomerDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(Integer customerId) {
        return customerRepository.findById(customerId)
                .map(CustomerMapper::mapToCustomerDTO);
    }

    @Override
    public CustomerDTO createCustomer(CustomerDTO customerDTO) {
        Customer customer = CustomerMapper.mapToCustomer(customerDTO);
        Customer savedCustomer = customerRepository.save(customer);
        return CustomerMapper.mapToCustomerDTO(savedCustomer);
    }

    @Override
    public CustomerDTO updateCustomer(Integer customerId, CustomerDTO customerDTO) {
        Optional<Customer> existingCustomerOpt = customerRepository.findById(customerId);
        if (existingCustomerOpt.isEmpty()) {
            throw new RuntimeException("Customer not found with ID: " + customerId);
        }

        Customer existingCustomer = existingCustomerOpt.get();
        existingCustomer.setType(customerDTO.getType());
        existingCustomer.setFirstName(customerDTO.getFirstName());
        existingCustomer.setLastName(customerDTO.getLastName());

        Customer updatedCustomer = customerRepository.save(existingCustomer);
        return CustomerMapper.mapToCustomerDTO(updatedCustomer);
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        customerRepository.deleteById(customerId);
    }
}

