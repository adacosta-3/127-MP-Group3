package mp.group3.cafe.backend.service;

import mp.group3.cafe.backend.DTO.CustomizationOptionsDTO;

import java.util.List;
import java.util.Optional;

public interface CustomizationOptionsService {
    List<CustomizationOptionsDTO> getAllOptions();

    Optional<CustomizationOptionsDTO> getOptionById(Integer optionId);

    List<CustomizationOptionsDTO> getOptionsByCustomizationId(Integer customizationId);

    CustomizationOptionsDTO createOption(CustomizationOptionsDTO optionsDTO);

    CustomizationOptionsDTO updateOption(Integer optionId, CustomizationOptionsDTO optionsDTO);

    void deleteOption(Integer optionId);
}

