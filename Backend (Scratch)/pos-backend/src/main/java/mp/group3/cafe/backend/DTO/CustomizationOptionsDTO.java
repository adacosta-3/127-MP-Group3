package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CustomizationOptionsDTO {
    private Integer optionId;
    private Integer customizationId;
    private String optionName;
    private Double additionalCost;
}
