package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.ItemSize;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemSizeRepository extends JpaRepository<ItemSize, Integer> {

    // Find all sizes associated with a specific item
    List<ItemSize> findByItem_ItemId(Integer itemId);
}

