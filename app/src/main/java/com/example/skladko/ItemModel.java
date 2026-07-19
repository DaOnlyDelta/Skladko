package com.example.skladko;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class ItemModel {
    private long id;
    private String name;
    private double count;
    private String category;

    public ItemModel(long id, String name, double count, String category) {
        this.id = id;
        this.name = name;
        this.count = count;
        this.category = category;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Formats the double value cleanly for display.
     * If it is a whole number, displays without any decimal digits (e.g., "5").
     * Otherwise, displays with up to 2 decimal places using standard dot notation (e.g., "1.5").
     */
    public String getFormattedCount() {
        if (count == (long) count) {
            return String.valueOf((long) count);
        } else {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
            DecimalFormat df = new DecimalFormat("#.##", symbols);
            return df.format(count);
        }
    }
}