package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemDTO {
    private Integer itemId;
    private String itemCode;
    private String name;
    private Double basePrice;
    private Integer categoryId;
    private List<ItemSizeDTO> sizes; // New field for sizes
}


