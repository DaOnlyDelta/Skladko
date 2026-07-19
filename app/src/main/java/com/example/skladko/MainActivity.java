package com.example.skladko;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView date;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<CategoryItem> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setDate();
        setupRecyclerView();
    }

    private void initViews() {
        date = findViewById(R.id.date);
        recyclerView = findViewById(R.id.categoryContainer);
    }

    private void setDate() {
        // Define the date format and set the locale to Slovenian
        SimpleDateFormat formatter = new SimpleDateFormat("dd. MM. yyyy", new Locale("sl", "SI"));
        String currentDate = formatter.format(new Date());

        // Update the text every time the screen becomes active
        if (date != null) {
            date.setText(currentDate);
        }
    }

    private void setupRecyclerView() {
        categoryList = new ArrayList<>();
        // Now we assign an ItemColor to each dummy category
        categoryList.add(new CategoryItem("Pivo", ItemColor.SVETLO_MODRA));
        categoryList.add(new CategoryItem("Vino", ItemColor.TIRKIZNA));
        categoryList.add(new CategoryItem("Žgane pijače", ItemColor.LILA));
        categoryList.add(new CategoryItem("Brezalkoholne pijače", ItemColor.MARELICA));
        categoryList.add(new CategoryItem("Kava in čaj", ItemColor.ROZA));
        categoryList.add(new CategoryItem("Prigrizki", ItemColor.SVETLO_RDECA));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // THIS WILL SHOW AN ERROR until we update CategoryAdapter in the next step!
        adapter = new CategoryAdapter(categoryList);
        recyclerView.setAdapter(adapter);
    }

    public void addCategory(View v) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_category);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        MaterialCardView colorPickerIndicator = dialog.findViewById(R.id.colorPickerIndicator);
        EditText categoryName = dialog.findViewById(R.id.categoryName);
        Button btnDodaj = dialog.findViewById(R.id.btnDodaj);

        // 1. Get all colors directly from the Enum
        final ItemColor[] allColors = ItemColor.values();
        String[] colorNames = new String[allColors.length];
        for (int i = 0; i < allColors.length; i++) {
            colorNames[i] = allColors[i].getDisplayName();
        }

        // 2. Default to the first color in your Enum (or whichever you prefer)
        final ItemColor[] selectedColor = {ItemColor.MARELICA};
        colorPickerIndicator.setCardBackgroundColor(selectedColor[0].getColorValue());

        colorPickerIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Izberi barvo")
                        .setItems(colorNames, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Save the actual Enum value, not just the integer!
                                selectedColor[0] = allColors[i];
                                colorPickerIndicator.setCardBackgroundColor(selectedColor[0].getColorValue());
                            }
                        })
                        .show();
            }
        });

        btnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredName = categoryName.getText().toString().trim();

                if (enteredName.isEmpty()) {
                    categoryName.setError("Vnesite ime");
                    return;
                }

                if (categoryList.contains(enteredName)) {
                    categoryName.setError("Že obstaja");
                    return;
                }

                String finalName = enteredName.toUpperCase().charAt(0) + enteredName.substring(1);

                // 3. Create the new CategoryItem with the selected Enum color
                CategoryItem newItem = new CategoryItem(finalName, selectedColor[0]);
                categoryList.add(newItem);

                // Tell the adapter to update
                if (adapter != null) {
                    adapter.notifyItemInserted(categoryList.size() - 1);
                }
                recyclerView.smoothScrollToPosition(categoryList.size() - 1);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void resetValues(View v) {

    }

    public void export(View v) {

    }
}