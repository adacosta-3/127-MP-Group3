package mp.group3.cafe.backend.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "CUSTOMIZATION")
public class Customization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "customization_id")
    private Integer customizationId;

    @Column(name = "name", nullable = false, length = 50)
    private String name;

    @ManyToOne
    @JoinColumn(name = "item_id", nullable = false)
    private Item item;
}
