package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemDTO {
    private Integer itemId;
    private String itemCode;
    private String name;
    private Double basePrice;
    private Integer categoryId; // Simplified for referencing the category
}

