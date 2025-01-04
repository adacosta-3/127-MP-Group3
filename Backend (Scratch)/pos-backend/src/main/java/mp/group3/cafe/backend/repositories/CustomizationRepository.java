package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.Customization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomizationRepository extends JpaRepository<Customization, Integer> {
    List<Customization> findByItem_ItemCode(String itemCode);
    List<Customization> findByNameContainingIgnoreCase(String name);
}

