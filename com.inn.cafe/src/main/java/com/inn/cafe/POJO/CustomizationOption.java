package com.inn.cafe.POJO;

import lombok.Data;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@DynamicUpdate
@DynamicInsert
@Table(name = "customization_options")
public class CustomizationOption implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "option_id")
    private Integer optionId;

    @Column(name = "customization_id")
    private Integer customizationId;

    @Column(name = "option_name")
    private String optionName;

    @Column(name = "additional_cost")
    private Double additionalCost;

    // Getters and Setters if not using Lombok
}
