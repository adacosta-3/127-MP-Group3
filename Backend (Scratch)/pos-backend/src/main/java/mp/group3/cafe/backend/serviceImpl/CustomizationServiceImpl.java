package mp.group3.cafe.backend.serviceImpl;

import com.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import mp.group3.cafe.backend.DTO.CustomizationDTO;
import mp.group3.cafe.backend.DTO.CustomizationOptionsDTO;
import mp.group3.cafe.backend.entities.Customization;
import mp.group3.cafe.backend.entities.CustomizationOptions;
import mp.group3.cafe.backend.entities.Item;
import mp.group3.cafe.backend.mapper.CustomizationMapper;
import mp.group3.cafe.backend.repositories.CustomizationOptionsRepository;
import mp.group3.cafe.backend.repositories.CustomizationRepository;
import mp.group3.cafe.backend.repositories.ItemRepository;
import mp.group3.cafe.backend.service.CustomizationService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomizationServiceImpl implements CustomizationService {

    private final CustomizationRepository customizationRepository;
    private final ItemRepository itemRepository;
    private final CustomizationOptionsRepository customizationOptionsRepository;

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
        Optional<Item> itemOpt = itemRepository.findById(customizationDTO.getItemCode());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + customizationDTO.getItemCode());
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

        Optional<Item> itemOpt = itemRepository.findById(customizationDTO.getItemCode());
        if (itemOpt.isEmpty()) {
            throw new RuntimeException("Item not found with ID: " + customizationDTO.getItemCode());
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
        Optional<Item> itemOpt = itemRepository.findById(itemCode);
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

    @Override
    public List<CustomizationOptionsDTO> addOptionsToCustomization(Integer customizationId, List<CustomizationOptionsDTO> options) {
        Customization customization = customizationRepository.findById(customizationId)
                .orElseThrow(() -> new RuntimeException("Customization not found"));

        // Map DTOs to entities
        List<CustomizationOptions> customizationOptions = options.stream()
                .map(option -> {
                    CustomizationOptions newOption = new CustomizationOptions();
                    newOption.setOptionName(option.getOptionName());
                    newOption.setAdditionalCost(option.getAdditionalCost());
                    newOption.setCustomization(customization);
                    return newOption;
                }).collect(Collectors.toList());

        // Save options
        List<CustomizationOptions> savedOptions = customizationOptionsRepository.saveAll(customizationOptions);

        // Return DTOs
        return savedOptions.stream()
                .map(option -> new CustomizationOptionsDTO(
                        option.getOptionId(),
                        option.getCustomization().getCustomizationId(),
                        option.getOptionName(),
                        option.getAdditionalCost()
                )).collect(Collectors.toList());
    }

    @SneakyThrows
    @Override
    public List<CustomizationDTO> uploadCustomizationsFromCSV(String itemCode, MultipartFile file) throws IOException {
        List<CustomizationDTO> customizations = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] values;
            csvReader.readNext();
            while ((values = csvReader.readNext()) != null) {
                CustomizationDTO customizationDTO = new CustomizationDTO(
                        null,
                        values[0],
                        itemCode,
                        null
                );
                customizations.add(createCustomization(customizationDTO));
            }
        }

        return customizations;
    }
}

