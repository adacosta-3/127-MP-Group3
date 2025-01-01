package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.CustomizationOptionsDTO;
import mp.group3.cafe.backend.entities.Customization;
import mp.group3.cafe.backend.entities.CustomizationOptions;

public class CustomizationOptionsMapper {
    public static CustomizationOptionsDTO mapToCustomizationOptionsDTO(CustomizationOptions customizationOptions) {
        return new CustomizationOptionsDTO(
                customizationOptions.getOptionId(),
                customizationOptions.getCustomization().getCustomizationId(),
                customizationOptions.getOptionName(),
                customizationOptions.getAdditionalCost()
        );
    }

    public static CustomizationOptions mapToCustomizationOptions(CustomizationOptionsDTO customizationOptionsDTO, Customization customization) {
        CustomizationOptions customizationOptions = new CustomizationOptions();
        customizationOptions.setOptionId(customizationOptionsDTO.getOptionId());
        customizationOptions.setCustomization(customization);
        customizationOptions.setOptionName(customizationOptionsDTO.getOptionName());
        customizationOptions.setAdditionalCost(customizationOptionsDTO.getAdditionalCost());
        return customizationOptions;
    }
}
