package com.inn.cafe.wrapper;

public class CustomizationWrapper {
    private Integer customizationId;
    private String name;
    private Integer itemId;

    // Constructor
    public CustomizationWrapper(Integer customizationId, String name, Integer itemId) {
        this.customizationId = customizationId;
        this.name = name;
        this.itemId = itemId;
    }

    // Getters and Setters
    public Integer getCustomizationId() {
        return customizationId;
    }

    public void setCustomizationId(Integer customizationId) {
        this.customizationId = customizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }
}
