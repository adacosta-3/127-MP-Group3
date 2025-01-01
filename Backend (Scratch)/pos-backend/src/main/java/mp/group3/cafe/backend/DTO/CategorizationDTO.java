package mp.group3.cafe.backend.DTO;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CategorizationDTO {
    private Integer categoryId;
    private String name;
    private String itemType; // "Drink", "Food", or "Merchandise"
}
