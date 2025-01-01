package mp.group3.cafe.backend.repositories;

import mp.group3.cafe.backend.entities.Categorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategorizationRepository extends JpaRepository<Categorization, Integer> {
    // Custom queries (if needed)
}

