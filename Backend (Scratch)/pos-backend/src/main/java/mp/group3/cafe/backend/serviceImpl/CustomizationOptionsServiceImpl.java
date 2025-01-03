package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CustomizationOptionsDTO;
import mp.group3.cafe.backend.entities.Customization;
import mp.group3.cafe.backend.entities.CustomizationOptions;
import mp.group3.cafe.backend.entities.Item;
import mp.group3.cafe.backend.mapper.CustomizationOptionsMapper;
import mp.group3.cafe.backend.repositories.CustomizationOptionsRepository;
import mp.group3.cafe.backend.repositories.CustomizationRepository;
import mp.group3.cafe.backend.repositories.ItemRepository;
import mp.group3.cafe.backend.service.CustomizationOptionsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationOptionsServiceImpl implements CustomizationOptionsService {

    private final CustomizationOptionsRepository optionsRepository;
    private final CustomizationRepository customizationRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<CustomizationOptionsDTO> getAllOptions() {
        return optionsRepository.findAll()
                .stream()
                .map(CustomizationOptionsMapper::mapToCustomizationOptionsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomizationOptionsDTO> getOptionById(Integer optionId) {
        return optionsRepository.findById(optionId)
                .map(CustomizationOptionsMapper::mapToCustomizationOptionsDTO);
    }

    @Override
    public List<CustomizationOptionsDTO> getOptionsByCustomizationId(Integer customizationId) {
        return optionsRepository.findByCustomization_CustomizationId(customizationId)
                .stream()
                .map(CustomizationOptionsMapper::mapToCustomizationOptionsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomizationOptionsDTO createOption(CustomizationOptionsDTO optionsDTO) {
        Optional<Customization> customizationOpt = customizationRepository.findById(optionsDTO.getCustomizationId());
        if (customizationOpt.isEmpty()) {
            throw new RuntimeException("Customization not found with ID: " + optionsDTO.getCustomizationId());
        }

        Customization customization = customizationOpt.get();
        CustomizationOptions option = CustomizationOptionsMapper.mapToCustomizationOptions(optionsDTO, customization);
        CustomizationOptions savedOption = optionsRepository.save(option);
        return CustomizationOptionsMapper.mapToCustomizationOptionsDTO(savedOption);
    }

    @Override
    public CustomizationOptionsDTO updateOption(Integer optionId, CustomizationOptionsDTO optionsDTO) {
        Optional<CustomizationOptions> existingOptionOpt = optionsRepository.findById(optionId);
        if (existingOptionOpt.isEmpty()) {
            throw new RuntimeException("Option not found with ID: " + optionId);
        }

        Optional<Customization> customizationOpt = customizationRepository.findById(optionsDTO.getCustomizationId());
        if (customizationOpt.isEmpty()) {
            throw new RuntimeException("Customization not found with ID: " + optionsDTO.getCustomizationId());
        }

        CustomizationOptions existingOption = existingOptionOpt.get();
        Customization customization = customizationOpt.get();

        existingOption.setOptionName(optionsDTO.getOptionName());
        existingOption.setAdditionalCost(optionsDTO.getAdditionalCost());
        existingOption.setCustomization(customization);

        CustomizationOptions updatedOption = optionsRepository.save(existingOption);
        return CustomizationOptionsMapper.mapToCustomizationOptionsDTO(updatedOption);
    }

    @Override
    public void deleteOption(Integer optionId) {
        optionsRepository.deleteById(optionId);
    }

    @Override
    public CustomizationOptionsDTO updateCustomizationOptionByItemCode(String itemCode, Integer optionId, CustomizationOptionsDTO customizationOptionsDTO) {
        // Fetch the item by item code
        Item item = itemRepository.findByItemCode(itemCode)
                .orElseThrow(() -> new RuntimeException("Item not found with code: " + itemCode));

        // Fetch the customization option
        CustomizationOptions customizationOption = optionsRepository.findById(optionId)
                .orElseThrow(() -> new RuntimeException("Customization option not found with ID: " + optionId));

        // Ensure the customization belongs to the correct item
        if (!customizationOption.getCustomization().getItem().equals(item)) {
            throw new RuntimeException("Customization option does not belong to the item with code: " + itemCode);
        }

        // Update fields
        customizationOption.setOptionName(customizationOptionsDTO.getOptionName());
        customizationOption.setAdditionalCost(customizationOptionsDTO.getAdditionalCost());

        // Save updated option
        CustomizationOptions updatedOption = optionsRepository.save(customizationOption);

        // Return DTO
        return new CustomizationOptionsDTO(
                updatedOption.getOptionId(),
                updatedOption.getCustomization().getCustomizationId(),
                updatedOption.getOptionName(),
                updatedOption.getAdditionalCost()
        );
    }
}
