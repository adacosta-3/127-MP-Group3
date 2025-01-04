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
@Table(name = "CUSTOMIZATION_OPTIONS")
public class CustomizationOptions {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Integer optionId;

    @ManyToOne
    @JoinColumn(name = "customization_id", nullable = false)
    private Customization customization;

    @Column(name = "option_name", nullable = false, length = 50)
    private String optionName;

    @Column(name = "additional_cost", nullable = false)
    private Double additionalCost = 0.0; // Default value
}


