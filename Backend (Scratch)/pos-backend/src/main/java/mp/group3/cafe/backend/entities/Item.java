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
@Table(name = "ITEM")
public class Item {

    @Id
    @Column(name = "item_code", nullable = false, unique = true, length = 20)
    private String itemCode;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @Column(name = "base_price", nullable = false)
    private Double basePrice;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Categorization category;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemSize> sizes = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Customization> customizations = new ArrayList<>();
}

