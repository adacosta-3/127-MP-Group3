package mp.group3.cafe.backend.DTO;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategorizationDTO {
    private Integer categoryId;
    private String name;
    private String itemType; // "Drink", "Food", or "Merchandise"
}
