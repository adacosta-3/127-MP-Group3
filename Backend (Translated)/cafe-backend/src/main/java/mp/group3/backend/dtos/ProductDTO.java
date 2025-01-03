package mp.group3.backend.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ProductDTO {
    Integer id;
    String name;
    String description;
    Integer price;
    String status;
    Integer categoryId;
    String categoryName;

    public ProductDTO(Integer id, String name , String description , Integer price , Integer categoryId , String categoryName , String status ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.status = status;
    }

    public ProductDTO(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public ProductDTO(Integer id, String name, String description, Integer price) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
    }
}