package mp.group3.cafe.backend.entities;

import jakarta.persistence.*;
import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "ORDER_LINE_CUSTOMIZATION")
public class OrderLineCustomization {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "line_customization_id")
    private Integer lineCustomizationId;

    @ManyToOne
    @JoinColumn(name = "order_line_id", nullable = false)
    private OrderLine orderLine;

    @ManyToOne
    @JoinColumn(name = "option_id", nullable = false)
    private CustomizationOptions customizationOption;
}


