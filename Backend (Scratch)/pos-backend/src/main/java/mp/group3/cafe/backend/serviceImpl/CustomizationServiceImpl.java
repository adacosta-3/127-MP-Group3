package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CustomizationDTO;
import mp.group3.cafe.backend.entities.Customization;
import mp.group3.cafe.backend.entities.CustomizationOptions;
import mp.group3.cafe.backend.entities.Item;
import mp.group3.cafe.backend.mapper.CustomizationMapper;
import mp.group3.cafe.backend.repositories.CustomizationRepository;
import mp.group3.cafe.backend.repositories.ItemRepository;
import mp.group3.cafe.backend.service.CustomizationService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationServiceImpl implements CustomizationService {

    private final CustomizationRepository customizationRepository;
    private final ItemRepository itemRepository;

    @Override
    public List<CustomizationDTO> getAllCustomizations() {
        return customizationRepository.findAll()
                .stream()
                .map(CustomizationMapper::mapToCustomizationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CustomizationDTO> getCustomizationById(Integer customizationId) {
        return customizationRepository.findById(customizationId)
                .map(CustomizationMapper::mapToCustomizationDTO);
    }

    @Override
    public List<CustomizationDTO> getCustomizationsByItemCode(String itemCode) {
        return customizationRepository.findByItem_ItemCode(itemCode)
                .stream()
                .map(CustomizationMapper::mapToCustomizationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomizationDTO createCustomization(CustomizationDTO customizationDTO) {
        // Fetch the associated item
        Optional<Item> itemOpt = itemRepository.findByItemCode(customizationDTO.getItemCode());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + customizationDTO.getItemCode());
        }

        Item item = itemOpt.get();

        // Map the Customization DTO to the Customization entity
        Customization customization = CustomizationMapper.mapToCustomization(customizationDTO, item);

        // Map the options from DTOs to entities and set the parent customization
        if (customizationDTO.getOptions() != null) {
            List<CustomizationOptions> options = customizationDTO.getOptions().stream()
                    .map(optionDTO -> {
                        CustomizationOptions option = new CustomizationOptions();
                        option.setOptionName(optionDTO.getOptionName());
                        option.setAdditionalCost(optionDTO.getAdditionalCost());
                        option.setCustomization(customization); // Set parent customization
                        return option;
                    })
                    .collect(Collectors.toList());

            customization.setOptions(options); // Set options to the customization
        }

        // Save the customization and associated options
        Customization savedCustomization = customizationRepository.save(customization);

        // Map the saved customization back to DTO
        return CustomizationMapper.mapToCustomizationDTO(savedCustomization);
    }


    @Override
    public CustomizationDTO updateCustomization(Integer customizationId, CustomizationDTO customizationDTO) {
        // Fetch the existing customization
        Optional<Customization> existingCustomizationOpt = customizationRepository.findById(customizationId);
        if (existingCustomizationOpt.isEmpty()) {
            throw new RuntimeException("Customization not found with ID: " + customizationId);
        }

        Optional<Item> itemOpt = itemRepository.findByItemCode(customizationDTO.getItemCode());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + customizationDTO.getItemCode());
        }

        Customization existingCustomization = existingCustomizationOpt.get();
        Item item = itemOpt.get();

        // Update basic fields
        existingCustomization.setName(customizationDTO.getName());
        existingCustomization.setItem(item);

        // Update options
        if (customizationDTO.getOptions() != null) {
            List<CustomizationOptions> options = customizationDTO.getOptions().stream()
                    .map(optionDTO -> {
                        CustomizationOptions option = new CustomizationOptions();
                        option.setOptionId(optionDTO.getOptionId()); // Use the existing ID if present
                        option.setOptionName(optionDTO.getOptionName());
                        option.setAdditionalCost(optionDTO.getAdditionalCost());
                        option.setCustomization(existingCustomization); // Set parent customization
                        return option;
                    })
                    .collect(Collectors.toList());

            existingCustomization.setOptions(options); // Replace existing options with the new ones
        }

        // Save the updated customization and options
        Customization updatedCustomization = customizationRepository.save(existingCustomization);

        // Map the updated customization back to DTO
        return CustomizationMapper.mapToCustomizationDTO(updatedCustomization);
    }


    @Override
    public void deleteCustomization(Integer customizationId) {
        customizationRepository.deleteById(customizationId);
    }


    @Override
    public List<CustomizationDTO> updateCustomizationsByItemCode(String itemCode, List<CustomizationDTO> customizations) {
        Optional<Item> itemOpt = itemRepository.findByItemCode(itemCode);
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with itemCode: " + itemCode);
        }

        Item item = itemOpt.get();

        // Remove old customizations
        List<Customization> oldCustomizations = customizationRepository.findByItem_ItemCode(item.getItemCode());
        customizationRepository.deleteAll(oldCustomizations);

        // Add new customizations
        List<Customization> newCustomizations = customizations.stream()
                .map(dto -> CustomizationMapper.mapToCustomization(dto, item))
                .collect(Collectors.toList());

        customizationRepository.saveAll(newCustomizations);

        return newCustomizations.stream()
                .map(CustomizationMapper::mapToCustomizationDTO)
                .collect(Collectors.toList());
    }
}

