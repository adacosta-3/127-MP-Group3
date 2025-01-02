package mp.group3.cafe.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ORDER_LINE")
public class OrderLine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_line_id")
    private Integer orderLineId;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private CustomerOrder order;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;

    @Column(name = "size_id")
    private Integer sizeId; // Nullable for non-sized items

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "line_price", nullable = false)
    private Double linePrice;

    @OneToMany(mappedBy = "orderLine", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderLineCustomization> customizations = new ArrayList<>();
}


