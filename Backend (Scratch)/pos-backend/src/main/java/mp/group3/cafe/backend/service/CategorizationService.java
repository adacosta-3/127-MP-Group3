package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.CategorizationDTO;

import java.util.List;
import java.util.Optional;

public interface CategorizationService {
    List<CategorizationDTO> getAllCategories();

    Optional<CategorizationDTO> getCategoryById(Integer categoryId);

    CategorizationDTO createCategory(CategorizationDTO categorizationDTO);

    CategorizationDTO updateCategory(Integer categoryId, CategorizationDTO categorizationDTO);

    void deleteCategory(Integer categoryId);
}

