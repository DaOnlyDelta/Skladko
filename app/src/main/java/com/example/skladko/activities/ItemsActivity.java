package com.example.skladko.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skladko.ItemColor;
import com.example.skladko.R;
import com.example.skladko.SessionManager;
import com.example.skladko.adapters.ItemAdapter;
import com.example.skladko.models.CategoryItem;
import com.example.skladko.models.InventoryItem;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

public class ItemsActivity extends AppCompatActivity {

    private TextView categoryTitle;
    private String currentCategoryName;
    private int categoryColor;

    private RecyclerView recyclerView;
    private ItemAdapter adapter;
    private SessionManager sessionManager;
    private List<InventoryItem> allItems;
    private List<InventoryItem> displayedItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_items);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        currentCategoryName = getIntent().getStringExtra("CATEGORY_NAME");
        categoryColor = getIntent().getIntExtra("CATEGORY_COLOR", 0xFF000000);

        categoryTitle = findViewById(R.id.categoryName);
        if (currentCategoryName != null) {
            categoryTitle.setText(currentCategoryName);
        }

        sessionManager = new SessionManager(this);
        recyclerView = findViewById(R.id.itemsContainer);

        setupRecyclerView();

        // Attach click listener to the Dodaj button
        Button btnDodajItem = findViewById(R.id.dodaj);
        btnDodajItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddItemDialog();
            }
        });

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                finish();
                overridePendingTransition(0, 0);
            }
        });
    }

    private void setupRecyclerView() {
        allItems = sessionManager.getItems();
        displayedItems = new ArrayList<>();

        for (InventoryItem item : allItems) {
            if (item.getCategoryName().equals(currentCategoryName)) {
                displayedItems.add(item);
            }
        }

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Updated adapter call without the shared color
        adapter = new ItemAdapter(displayedItems, new ItemAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(InventoryItem item, int position) {
                showUpdateQuantityDialog(item, position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    private void showAddItemDialog() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_item);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        MaterialCardView colorPickerIndicator = dialog.findViewById(R.id.colorPickerIndicator);
        EditText itemNameInput = dialog.findViewById(R.id.itemNameInput);
        EditText itemQuantityInput = dialog.findViewById(R.id.itemQuantityInput);
        Button btnShraniArtikel = dialog.findViewById(R.id.btnShraniArtikel);

        final ItemColor[] allColors = ItemColor.values();
        String[] colorNames = new String[allColors.length];
        for (int i = 0; i < allColors.length; i++) {
            colorNames[i] = allColors[i].getDisplayName();
        }

        // Find the category's Enum color to use as default
        final ItemColor[] selectedColor = {getDefaultCategoryColor()};
        colorPickerIndicator.setCardBackgroundColor(selectedColor[0].getColorValue());

        colorPickerIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(ItemsActivity.this)
                        .setTitle("Izberi barvo")
                        .setItems(colorNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                selectedColor[0] = allColors[i];
                                colorPickerIndicator.setCardBackgroundColor(selectedColor[0].getColorValue());
                            }
                        })
                        .show();
            }
        });

        btnShraniArtikel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredName = itemNameInput.getText().toString().trim();
                String enteredQuantity = itemQuantityInput.getText().toString().trim();

                if (enteredName.isEmpty()) {
                    itemNameInput.setError("Vnesite ime");
                    return;
                }

                // Check if item already exists in this category
                for (InventoryItem item : displayedItems) {
                    if (item.getName().equalsIgnoreCase(enteredName)) {
                        itemNameInput.setError("Že obstaja v tej kategoriji");
                        return;
                    }
                }

                double startingQuantity = 0.0;
                if (!enteredQuantity.isEmpty()) {
                    try {
                        startingQuantity = Double.parseDouble(enteredQuantity);
                    } catch (NumberFormatException e) {
                        startingQuantity = 0.0;
                    }
                }

                String finalName = enteredName.toUpperCase().charAt(0) + enteredName.substring(1);

                // Create the new item and add it to both lists
                InventoryItem newItem = new InventoryItem(finalName, startingQuantity, currentCategoryName, selectedColor[0]);
                allItems.add(newItem);
                displayedItems.add(newItem);

                // Save to SharedPreferences
                sessionManager.saveItems(allItems);

                // Update UI
                adapter.notifyItemInserted(displayedItems.size() - 1);
                recyclerView.smoothScrollToPosition(displayedItems.size() - 1);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showUpdateQuantityDialog(InventoryItem item, int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_quantity);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        EditText dialogItemName = dialog.findViewById(R.id.dialogItemName);
        EditText currentQuantity = dialog.findViewById(R.id.currentQuantity);

        Button btnMinus = dialog.findViewById(R.id.btnMinus);
        Button btnMinus2 = dialog.findViewById(R.id.btnMinus2);
        Button btnMinus3 = dialog.findViewById(R.id.btnMinus3);
        Button btnPlus = dialog.findViewById(R.id.btnPlus);
        Button btnPlus2 = dialog.findViewById(R.id.btnPlus2);
        Button btnPlus4 = dialog.findViewById(R.id.btnPlus4);
        Button btnIzbrisiItem = dialog.findViewById(R.id.btnIzbrisiItem);
        Button btnShraniKolicino = dialog.findViewById(R.id.btnShraniKolicino);

        // Set the initial values
        dialogItemName.setText(item.getName());
        currentQuantity.setText(item.getDisplayQuantity());

        // Wire up the 6 quick-action buttons using our helper method
        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { adjustQuantity(currentQuantity, -1.0); }
        });

        btnMinus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { adjustQuantity(currentQuantity, -5.0); }
        });

        btnMinus3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { adjustQuantity(currentQuantity, -20.0); }
        });

        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { adjustQuantity(currentQuantity, 1.0); }
        });

        btnPlus2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { adjustQuantity(currentQuantity, 5.0); }
        });

        btnPlus4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { adjustQuantity(currentQuantity, 20.0); }
        });

        // Delete the item entirely
        btnIzbrisiItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(ItemsActivity.this)
                        .setTitle("Izbriši artikel")
                        .setMessage("Ali ste prepričani, da želite izbrisati ta artikel?")
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface d, int which) {
                                allItems.remove(item);
                                displayedItems.remove(item);
                                sessionManager.saveItems(allItems);
                                adapter.notifyItemRemoved(position);
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Prekliči", null)
                        .show();
            }
        });

        // Save the final typed or calculated value
        btnShraniKolicino.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double finalVal = 0.0;
                String text = currentQuantity.getText().toString().trim();

                if (!text.isEmpty()) {
                    try {
                        finalVal = Double.parseDouble(text);
                    } catch (NumberFormatException e) {
                        finalVal = 0.0;
                    }
                }

                // Update the object in our local list
                String updatedName = dialogItemName.getText().toString().trim();
                if (!updatedName.isEmpty()) {
                    item.setName(updatedName);
                }
                item.setQuantity(finalVal);

                // Save the entire master list to SharedPreferences
                sessionManager.saveItems(allItems);

                // Tell the adapter to refresh just this one square on the grid
                adapter.notifyItemChanged(position);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void adjustQuantity(EditText input, double amount) {
        double currentVal = 0.0;
        String text = input.getText().toString().trim();

        // Safely read the current number from the EditText
        if (!text.isEmpty()) {
            try {
                currentVal = Double.parseDouble(text);
            } catch (NumberFormatException e) {
                currentVal = 0.0;
            }
        }

        // Calculate the new value, but don't let it drop below 0
        double newVal = Math.max(0.0, currentVal + amount);

        // Format it nicely (remove .0 if it's a whole number)
        if (newVal == (long) newVal) {
            input.setText(String.format("%d", (long) newVal));
        } else {
            input.setText(String.valueOf(newVal));
        }
    }

    public void showDeleteConfirmation(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Izbriši kategorijo")
                .setMessage("Ste prepričani, da želite izbrisati to kategorijo in vse njene artikle? Tega ni mogoče razveljaviti.")
                .setPositiveButton("Izbriši", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteCategoryAndItems();
                    }
                })
                .setNegativeButton("Prekliči", null)
                .show();
    }

    private void deleteCategoryAndItems() {
        // 1. Delete the category itself
        List<CategoryItem> allCategories = sessionManager.getCategories();
        for (int i = 0; i < allCategories.size(); i++) {
            if (allCategories.get(i).getName().equals(currentCategoryName)) {
                allCategories.remove(i);
                break;
            }
        }
        sessionManager.saveCategories(allCategories);

        // 2. Delete all items that belong to this category
        // We loop backwards through the list when deleting to avoid index crashes
        for (int i = allItems.size() - 1; i >= 0; i--) {
            if (allItems.get(i).getCategoryName().equals(currentCategoryName)) {
                allItems.remove(i);
            }
        }
        sessionManager.saveItems(allItems);

        // 3. Close this screen and instantly return to MainActivity
        finish();
        overridePendingTransition(0, 0);
    }

    // Helper method to find the matching Enum based on the color integer passed from MainActivity
    private ItemColor getDefaultCategoryColor() {
        for (ItemColor colorEnum : ItemColor.values()) {
            if (colorEnum.getColorValue() == categoryColor) {
                return colorEnum;
            }
        }
        return ItemColor.MARELICA; // Fallback just in case
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }
}