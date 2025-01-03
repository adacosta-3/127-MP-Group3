package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.CustomizationOptions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomizationOptionsRepository extends JpaRepository<CustomizationOptions, Integer> {
    List<CustomizationOptions> findByCustomization_CustomizationId(Integer customizationId);
    List<CustomizationOptions> findByItemCode(@Param("itemCode") String itemCode);

}

