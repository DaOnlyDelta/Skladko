package com.example.skladko.models;

import com.example.skladko.ItemColor;

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

    public void setName(String name) {
        this.name = name;
    }

    public ItemColor getColor() {
        return color;
    }

    public void setColor(ItemColor color) {
        this.color = color;
    }
}