package mp.group3.cafe.backend.DTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
@AllArgsConstructor
public class CustomizationDTO {
    private Integer customizationId;
    private String name;
    private String itemCode;
    private List<CustomizationOptionsDTO> options;
}

