package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Integer> {
    List<Item> findByCategory_CategoryId(Integer categoryId);
    List<Item> findByNameContainingIgnoreCase(String name);
    Optional<Item> findByItemCode(String itemCode);

}

