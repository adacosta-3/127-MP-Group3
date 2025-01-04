package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.Categorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategorizationRepository extends JpaRepository<Categorization, Integer> {
    List<Categorization> findByItemType(String itemType);
}

