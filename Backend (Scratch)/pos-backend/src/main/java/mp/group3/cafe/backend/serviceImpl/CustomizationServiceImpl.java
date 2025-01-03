package mp.group3.cafe.backend.serviceImpl;

import lombok.RequiredArgsConstructor;
import mp.group3.cafe.backend.DTO.CustomizationDTO;
import mp.group3.cafe.backend.entities.Customization;
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
    public List<CustomizationDTO> getCustomizationsByItemId(Integer itemId) {
        return customizationRepository.findByItem_ItemId(itemId)
                .stream()
                .map(CustomizationMapper::mapToCustomizationDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomizationDTO createCustomization(CustomizationDTO customizationDTO) {
        Optional<Item> itemOpt = itemRepository.findById(customizationDTO.getItemId());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + customizationDTO.getItemId());
        }

        Item item = itemOpt.get();
        Customization customization = CustomizationMapper.mapToCustomization(customizationDTO, item);
        Customization savedCustomization = customizationRepository.save(customization);
        return CustomizationMapper.mapToCustomizationDTO(savedCustomization);
    }

    @Override
    public CustomizationDTO updateCustomization(Integer customizationId, CustomizationDTO customizationDTO) {
        Optional<Customization> existingCustomizationOpt = customizationRepository.findById(customizationId);
        if (existingCustomizationOpt.isEmpty()) {
            throw new RuntimeException("Customization not found with ID: " + customizationId);
        }

        Optional<Item> itemOpt = itemRepository.findById(customizationDTO.getItemId());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + customizationDTO.getItemId());
        }

        Customization existingCustomization = existingCustomizationOpt.get();
        Item item = itemOpt.get();

        existingCustomization.setName(customizationDTO.getName());
        existingCustomization.setItem(item);

        Customization updatedCustomization = customizationRepository.save(existingCustomization);
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
        List<Customization> oldCustomizations = customizationRepository.findByItem_ItemId(item.getItemId());
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

