package mp.group3.cafe.backend.mapper;

import mp.group3.cafe.backend.DTO.CategorizationDTO;
import mp.group3.cafe.backend.entities.Categorization;

public class CategorizationMapper {
    public static CategorizationDTO mapToCategorizationDTO(Categorization categorization) {
        return new CategorizationDTO(
                categorization.getCategoryId(),
                categorization.getName(),
                categorization.getItemType()
        );
    }

    public static Categorization mapToCategorization(CategorizationDTO categorizationDTO) {
        Categorization categorization = new Categorization();
        categorization.setCategoryId(categorizationDTO.getCategoryId());
        categorization.setName(categorizationDTO.getName());
        categorization.setItemType(categorizationDTO.getItemType());
        return categorization;
    }
}

