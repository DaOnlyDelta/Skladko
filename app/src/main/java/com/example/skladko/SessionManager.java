package com.example.skladko;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.skladko.models.CategoryItem;
import com.example.skladko.models.InventoryItem;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class SessionManager {
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Gson gson;

    public SessionManager(Context context) {
        // Initializes SharedPreferences in private mode so only this app can access it
        sharedPreferences = context.getSharedPreferences("SkladkoSession", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        gson = new Gson();
    }

    public void saveCategories(List<CategoryItem> categoryList) {
        String json = gson.toJson(categoryList);
        editor.putString("category_list", json);
        editor.apply();
    }

    public List<CategoryItem> getCategories() {
        String json = sharedPreferences.getString("category_list", null);

        // If nothing is saved yet, return an empty list instead of null to prevent crashes
        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<CategoryItem>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveItems(List<InventoryItem> itemList) {
        String json = gson.toJson(itemList);
        editor.putString("item_list", json);
        editor.apply();
    }

    public List<InventoryItem> getItems() {
        String json = sharedPreferences.getString("item_list", null);

        if (json == null) {
            return new ArrayList<>();
        }

        Type type = new TypeToken<ArrayList<InventoryItem>>() {}.getType();
        return gson.fromJson(json, type);
    }
}