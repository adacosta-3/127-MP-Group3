package mp.group3.backend.repositories;

import mp.group3.backend.entities.Bill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Integer> {
    List<Bill> getAllBills();
    List<Bill> getBillByUserName(@Param("username") String username);
}
