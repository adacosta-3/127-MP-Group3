package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    List<Customer> findByType(String type);
    List<Customer> findByFirstNameContainingIgnoreCase(String firstName);
    List<Customer> findByLastNameContainingIgnoreCase(String lastName);

    @Query("SELECT c FROM Customer c WHERE c.type = 'Member'")
    List<Customer> findAllMembers();

}

