package mp.group3.cafe.backend.service;

import com.opencsv.exceptions.CsvException;
import mp.group3.cafe.backend.DTO.CategorizationDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CategorizationService {
    List<CategorizationDTO> getAllCategories();

    Optional<CategorizationDTO> getCategoryById(Integer categoryId);

    CategorizationDTO createCategory(CategorizationDTO categorizationDTO);

    CategorizationDTO updateCategory(Integer categoryId, CategorizationDTO categorizationDTO);

    List<CategorizationDTO> createCategories(List<CategorizationDTO> categorizationDTOs);

    void deleteCategory(Integer categoryId);

    List<CategorizationDTO> parseCSVToCategories(MultipartFile file) throws IOException, CsvException;
    List<CategorizationDTO> getCategoriesByItemType(String itemType);
}

