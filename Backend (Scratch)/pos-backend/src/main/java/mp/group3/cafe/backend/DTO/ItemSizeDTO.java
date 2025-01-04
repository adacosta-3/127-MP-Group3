package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemSizeDTO {
    private Integer sizeId;
    private String sizeName;
    private Double priceAdjustment;
}

