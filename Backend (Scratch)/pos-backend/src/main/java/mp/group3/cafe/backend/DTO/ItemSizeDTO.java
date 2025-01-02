package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ItemSizeDTO {
    private Integer sizeId;
    private String sizeName;
    private Double priceAdjustment;
}

