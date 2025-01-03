package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemDTO {
    private String itemCode;
    private String name;
    private Double basePrice;
    private Integer categoryId;
    private List<ItemSizeDTO> sizes; // New field for sizes
    private List<CustomizationDTO> customizations;
}