package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.CustomerOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CustomerOrderRepository extends JpaRepository<CustomerOrder, Integer> {
    List<CustomerOrder> findByCustomer_CustomerId(Integer customerId);
    @Query("SELECT o FROM CustomerOrder o WHERE o.orderDate BETWEEN :startDate AND :endDate")
    List<CustomerOrder> findByOrderDateBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    List<CustomerOrder> findByCashier_UserId(Integer userId);
}
