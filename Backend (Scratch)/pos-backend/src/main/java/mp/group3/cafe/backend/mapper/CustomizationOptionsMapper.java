package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.CustomizationOptionsDTO;
import mp.group3.cafe.backend.entities.Customization;
import mp.group3.cafe.backend.entities.CustomizationOptions;

public class CustomizationOptionsMapper {
    public static CustomizationOptionsDTO mapToCustomizationOptionsDTO(CustomizationOptions option) {
        return new CustomizationOptionsDTO(
                option.getOptionId(),
                option.getCustomization().getCustomizationId(),
                option.getOptionName(),
                option.getAdditionalCost()
        );
    }

    public static CustomizationOptions mapToCustomizationOptions(CustomizationOptionsDTO optionDTO, Customization customization) {
        CustomizationOptions option = new CustomizationOptions();
        option.setOptionId(optionDTO.getOptionId());
        option.setOptionName(optionDTO.getOptionName());
        option.setAdditionalCost(optionDTO.getAdditionalCost());
        option.setCustomization(customization);
        return option;
    }
}

