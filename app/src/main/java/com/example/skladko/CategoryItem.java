package com.example.skladko;

public class CategoryItem {
    private String name;
    private ItemColor color;

    public CategoryItem(String name, ItemColor color) {
        this.name = name;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public ItemColor getColor() {
        return color;
    }
}