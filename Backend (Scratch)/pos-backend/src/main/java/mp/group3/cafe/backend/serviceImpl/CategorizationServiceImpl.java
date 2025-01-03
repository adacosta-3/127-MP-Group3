package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CategorizationDTO;
import mp.group3.cafe.backend.entities.Categorization;
import mp.group3.cafe.backend.mapper.CategorizationMapper;
import mp.group3.cafe.backend.repositories.CategorizationRepository;
import mp.group3.cafe.backend.service.CategorizationService;
import org.springframework.stereotype.Service;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategorizationServiceImpl implements CategorizationService {

    private final CategorizationRepository categorizationRepository;

    @Override
    public List<CategorizationDTO> getAllCategories() {
        return categorizationRepository.findAll()
                .stream()
                .map(CategorizationMapper::mapToCategorizationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategorizationDTO> getCategoryById(Integer categoryId) {
        return categorizationRepository.findById(categoryId)
                .map(CategorizationMapper::mapToCategorizationDTO);
    }

    @Override
    public CategorizationDTO createCategory(CategorizationDTO categorizationDTO) {
        Categorization categorization = CategorizationMapper.mapToCategorization(categorizationDTO);
        Categorization savedCategory = categorizationRepository.save(categorization);
        return CategorizationMapper.mapToCategorizationDTO(savedCategory);
    }

    public List<CategorizationDTO> createCategories(List<CategorizationDTO> categorizationDTOs) {
        List<Categorization> categorizations = categorizationDTOs.stream()
                .map(CategorizationMapper::mapToCategorization)
                .toList();

        List<Categorization> savedCategories = categorizationRepository.saveAll(categorizations);

        return savedCategories.stream()
                .map(CategorizationMapper::mapToCategorizationDTO)
                .toList();
    }

    @Override
    public CategorizationDTO updateCategory(Integer categoryId, CategorizationDTO categorizationDTO) {
        Optional<Categorization> existingCategoryOpt = categorizationRepository.findById(categoryId);
        if (existingCategoryOpt.isEmpty()) {
            throw new RuntimeException("Category not found with ID: " + categoryId);
        }

        Categorization existingCategory = existingCategoryOpt.get();
        existingCategory.setName(categorizationDTO.getName());
        existingCategory.setItemType(categorizationDTO.getItemType());

        Categorization updatedCategory = categorizationRepository.save(existingCategory);
        return CategorizationMapper.mapToCategorizationDTO(updatedCategory);
    }

    @Override
    public void deleteCategory(Integer categoryId) {
        categorizationRepository.deleteById(categoryId);
    }

    @Override
    public List<CategorizationDTO> parseCSVToCategories(MultipartFile file) throws IOException, CsvException {
        List<CategorizationDTO> categories = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                CategorizationDTO dto = new CategorizationDTO();
                dto.setName(values[0]);
                dto.setItemType(values[1]);
                categories.add(dto);
            }
        }

        return categories;
    }
}

