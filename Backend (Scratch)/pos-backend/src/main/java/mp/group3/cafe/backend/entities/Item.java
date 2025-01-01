package mp.group3.cafe.backend.entities;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ITEM")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Integer itemId;

    @Column(name = "item_code", nullable = false, unique = true, length = 20)
    private String itemCode;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Categorization category;
}

