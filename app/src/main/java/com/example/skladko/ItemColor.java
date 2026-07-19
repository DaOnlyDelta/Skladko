package com.example.skladko;

import android.graphics.Color;

public enum ItemColor {
    SVETLO_RDECA("Svetlo rdeča", Color.parseColor("#FFADAD")),
    MARELICA("Marelica", Color.parseColor("#FFD6A5")),
    SVETLO_RUMENA("Svetlo rumena", Color.parseColor("#FDFFB6")),
    SVETLO_ZELENA("Svetlo zelena", Color.parseColor("#CAFFBF")),
    TIRKIZNA("Tirkizna", Color.parseColor("#9BF6FF")),
    SVETLO_MODRA("Svetlo modra", Color.parseColor("#A0C4FF")),
    LILA("Lila", Color.parseColor("#BDB2FF")),
    ROZA("Roza", Color.parseColor("#FFC6FF"));

    private final String displayName;
    private final int colorValue;

    ItemColor(String displayName, int colorValue) {
        this.displayName = displayName;
        this.colorValue = colorValue;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getColorValue() {
        return colorValue;
    }
}