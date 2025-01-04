package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OrderLineCustomizationDTO {
    private Integer lineCustomizationId;
    private Integer orderLineId;
    private Integer optionId;
}

