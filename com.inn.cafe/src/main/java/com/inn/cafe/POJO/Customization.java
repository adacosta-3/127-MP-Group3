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
@Table(name = "customization")
public class Customization implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customization_id")
    private Integer customizationId;

    @Column(name = "name")
    private String name;

    @Column(name = "item_id")
    private Integer itemId;

    // Getters and Setters if not using Lombok
}
