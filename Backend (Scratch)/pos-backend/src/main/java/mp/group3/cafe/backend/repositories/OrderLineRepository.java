package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.DTO.AdminDashboard.ItemOrderStatsDTO;
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
    List<OrderLine> findByItem_ItemCode(String itemCode);
    
    @Query("SELECT ol FROM OrderLine ol " +
            "JOIN ol.customizations olc " +
            "WHERE ol.order.orderId = :orderId " +
            "AND ol.item.itemCode = :itemCode " +
            "AND ol.sizeId = :sizeId " +
            "GROUP BY ol.orderLineId " +
            "HAVING COUNT(olc.customizationOption.optionId) = :customizationCount " +
            "AND SUM(CASE WHEN olc.customizationOption.optionId IN (:customizationOptionIds) THEN 1 ELSE 0 END) = :customizationCount")
    Optional<OrderLine> findDuplicateOrderLine(
            @Param("orderId") Integer orderId,
            @Param("itemCode") String itemCode,
            @Param("sizeId") Integer sizeId,
            @Param("customizationOptionIds") List<Integer> customizationOptionIds,
            @Param("customizationCount") Long customizationCount);

    @Query("SELECT new mp.group3.cafe.backend.DTO.AdminDashboard.ItemOrderStatsDTO(ol.item.itemCode, ol.item.name, SUM(ol.quantity)) " +
            "FROM OrderLine ol " +
            "GROUP BY ol.item.itemCode, ol.item.name " +
            "ORDER BY SUM(ol.quantity) DESC")
    List<ItemOrderStatsDTO> findMostOrderedItems();

    @Query("SELECT new mp.group3.cafe.backend.DTO.AdminDashboard.ItemOrderStatsDTO(ol.item.itemCode, ol.item.name, SUM(ol.quantity)) " +
            "FROM OrderLine ol " +
            "GROUP BY ol.item.itemCode, ol.item.name " +
            "ORDER BY SUM(ol.quantity) ASC")
    List<ItemOrderStatsDTO> findLeastOrderedItems();















}

