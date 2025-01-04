package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryForMemberDTO;
import mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryPerDayDTO;
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

    @Query("SELECT new mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryPerDayDTO(DATE(o.orderDate), COUNT(o)) " +
            "FROM CustomerOrder o " +
            "GROUP BY DATE(o.orderDate) " +
            "ORDER BY DATE(o.orderDate) ASC")
    List<OrderHistoryPerDayDTO> findOrderHistoryPerDay();

    @Query("SELECT new mp.group3.cafe.backend.DTO.AdminDashboard.OrderHistoryForMemberDTO(o.orderId, DATE(o.orderDate), o.totalPrice) " +
            "FROM CustomerOrder o " +
            "JOIN o.customer c " +
            "JOIN Member m ON c.customerId = m.customer.customerId " +
            "WHERE m.memberId = :memberId " +
            "ORDER BY o.orderDate DESC")
    List<OrderHistoryForMemberDTO> findOrderHistoryForMember(@Param("memberId") String memberId);







}
