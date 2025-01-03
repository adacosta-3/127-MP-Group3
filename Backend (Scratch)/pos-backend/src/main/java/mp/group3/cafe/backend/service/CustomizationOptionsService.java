package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.CustomizationOptionsDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface CustomizationOptionsService {
    List<CustomizationOptionsDTO> getAllOptions();

    Optional<CustomizationOptionsDTO> getOptionById(Integer optionId);

    List<CustomizationOptionsDTO> getOptionsByCustomizationId(Integer customizationId);

    CustomizationOptionsDTO createOption(CustomizationOptionsDTO optionsDTO);

    CustomizationOptionsDTO updateOption(Integer optionId, CustomizationOptionsDTO optionsDTO);

    void deleteOption(Integer optionId);
    CustomizationOptionsDTO updateCustomizationOptionByItemCode(String itemCode, Integer optionId, CustomizationOptionsDTO customizationOptionsDTO);

    List<CustomizationOptionsDTO> uploadOptionsFromCSV(Integer customizationId, MultipartFile file) throws IOException;
}

