package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Integer> {
    List<OrderLine> findByOrder_OrderId(Integer orderId);
    List<OrderLine> findByItem_ItemId(Integer itemId);

}

