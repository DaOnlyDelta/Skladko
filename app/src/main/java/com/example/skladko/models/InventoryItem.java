package com.example.skladko.models;

import com.example.skladko.ItemColor;

public class InventoryItem {
    private String name;
    private double quantity; // Changed to double to support decimals
    private String categoryName;
    private ItemColor color;

    public InventoryItem(String name, double quantity, String categoryName, ItemColor color) {
        this.name = name;
        this.quantity = quantity;
        this.categoryName = categoryName;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ItemColor getColor() {
        return color;
    }

    public void setColor(ItemColor color) {
        this.color = color;
    }

    // Helper method to keep the numbers looking clean on the screen
    public String getDisplayQuantity() {
        if (quantity == (long) quantity) {
            return String.format("%d", (long) quantity);
        } else {
            return String.valueOf(quantity);
        }
    }
}