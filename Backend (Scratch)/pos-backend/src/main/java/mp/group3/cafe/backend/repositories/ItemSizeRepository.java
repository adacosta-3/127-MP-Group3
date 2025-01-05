package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.ItemSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ItemSizeRepository extends JpaRepository<ItemSize, Integer> {

    // Find all sizes associated with a specific item
    List<ItemSize> findByItem_ItemCode(String itemCode);
    Optional<ItemSize> findBySizeNameAndPriceAdjustment(String sizeName, Double priceAdjustment);
}

