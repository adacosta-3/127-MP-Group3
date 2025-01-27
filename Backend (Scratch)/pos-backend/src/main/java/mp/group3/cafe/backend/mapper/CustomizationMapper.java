package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.CustomizationDTO;
import mp.group3.cafe.backend.entities.Customization;
import mp.group3.cafe.backend.entities.Item;

import java.util.stream.Collectors;

public class CustomizationMapper {
    public static CustomizationDTO mapToCustomizationDTO(Customization customization) {
        return new CustomizationDTO(
                customization.getCustomizationId(),
                customization.getName(),
                customization.getItem().getItemCode(),
                customization.getOptions().stream()
                        .map(CustomizationOptionsMapper::mapToCustomizationOptionsDTO)
                        .collect(Collectors.toList())
        );
    }

    public static Customization mapToCustomization(CustomizationDTO customizationDTO, Item item) {
        Customization customization = new Customization();
        customization.setCustomizationId(customizationDTO.getCustomizationId());
        customization.setName(customizationDTO.getName());
        customization.setItem(item);
        return customization;
    }
}


