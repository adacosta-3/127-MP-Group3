package mp.group3.cafe.backend.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "ITEM_SIZES")
public class ItemSize {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "size_id")
    private Integer sizeId; // Unique ID for each size

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id", nullable = false)
    private Item item; // Associated item

    @Column(name = "size_name", nullable = false, length = 20)
    private String sizeName; // Name of the size (e.g., Small, Medium, Large)

    @Column(name = "price_adjustment", nullable = false)
    private Double priceAdjustment = 0.00; // Price adjustment for this size
}

