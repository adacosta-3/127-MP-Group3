package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderLineRepository extends JpaRepository<OrderLine, Integer> {
    List<OrderLine> findByOrder_OrderId(Integer orderId);
    List<OrderLine> findByItem_ItemId(Integer itemId);

//    @Query("""
//    SELECT ol
//    FROM OrderLine ol
//    JOIN ol.customizations olc
//    WHERE ol.order.orderId = :orderId
//      AND ol.item.itemId = :itemId
//      AND ol.sizeId = :sizeId
//    GROUP BY ol
//    HAVING COUNT(olc) = :customizationCount
//       AND SUM(CASE WHEN olc.customizationOption.optionId IN :customizationIds THEN 1 ELSE 0 END) = :customizationCount
//""")
//    Optional<OrderLine> findDuplicateOrderLine(
//            @Param("orderId") Integer orderId,
//            @Param("itemId") Integer itemId,
//            @Param("sizeId") Integer sizeId,
//            @Param("customizationIds") List<Integer> customizationIds,
//            @Param("customizationCount") Long customizationCount
//    );

    @Query("""
    SELECT ol 
    FROM OrderLine ol 
    JOIN ol.customizations olc 
    WHERE ol.order.orderId = :orderId 
      AND ol.item.itemId = :itemId 
      AND ol.sizeId = :sizeId 
    GROUP BY ol, olc.lineCustomizationId, olc.customizationOption.optionId 
    HAVING COUNT(olc) = :customizationCount 
       AND SUM(CASE WHEN olc.customizationOption.optionId IN :customizationIds THEN 1 ELSE 0 END) = :customizationCount
""")
    Optional<OrderLine> findDuplicateOrderLine(
            @Param("orderId") Integer orderId,
            @Param("itemId") Integer itemId,
            @Param("sizeId") Integer sizeId,
            @Param("customizationIds") List<Integer> customizationIds,
            @Param("customizationCount") Long customizationCount
    );










}

