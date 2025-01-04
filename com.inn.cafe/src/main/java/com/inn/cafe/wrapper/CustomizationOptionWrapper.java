package com.inn.cafe.wrapper;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CustomizationOptionWrapper {

    private Integer id;
    private String name; // Name of the customization option
    private Double priceModifier; // Cost adjustment for this option
    private Integer customizationId; // Associated customization ID
    private String customizationName; // Optional: Associated customization name

    public CustomizationOptionWrapper(Integer id, String name, Double priceModifier, Integer customizationId, String customizationName) {
        this.id = id;
        this.name = name;
        this.priceModifier = priceModifier;
        this.customizationId = customizationId;
        this.customizationName = customizationName;
    }

    public CustomizationOptionWrapper(Integer id, String name, Double priceModifier) {
        this.id = id;
        this.name = name;
        this.priceModifier = priceModifier;
    }

    public CustomizationOptionWrapper(Integer id, String name) {
        this.id = id;
        this.name = name;
    }
}
