package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.OrderLineCustomization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderLineCustomizationRepository extends JpaRepository<OrderLineCustomization, Integer> {
    List<OrderLineCustomization> findByOrderLine_OrderLineId(Integer orderLineId);

}

