package com.example.skladko.activities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skladko.ItemColor;
import com.example.skladko.models.CategoryItem;
import com.example.skladko.models.InventoryItem;
import com.example.skladko.R;
import com.example.skladko.SessionManager;
import com.example.skladko.adapters.CategoryAdapter;
import com.google.android.material.card.MaterialCardView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView date;
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<CategoryItem> categoryList;
    private SessionManager sessionManager; // 1. Declare the SessionManager

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

        // 2. Initialize it
        sessionManager = new SessionManager(this);

        initViews();
        setDate();
        setupRecyclerView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Refresh data when returning to this screen
        if (sessionManager != null && adapter != null) {
            categoryList.clear();
            categoryList.addAll(sessionManager.getCategories());
            adapter.notifyDataSetChanged();
        }
    }

    private void initViews() {
        date = findViewById(R.id.date);
        recyclerView = findViewById(R.id.categoriesContainer);
    }

    private void setDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd. MM. yyyy", new Locale("sl", "SI"));
        String currentDate = formatter.format(new Date());

        if (date != null) {
            date.setText(currentDate);
        }
    }

    private void setupRecyclerView() {
        categoryList = sessionManager.getCategories();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Pass the listener into the adapter
        adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnCategoryClickListener() {
            @Override
            public void onCategoryClick(CategoryItem category) {
                Intent intent = new Intent(MainActivity.this, ItemsActivity.class);
                intent.putExtra("CATEGORY_NAME", category.getName());

                // NEW: Pass the actual integer color value to the new screen
                intent.putExtra("CATEGORY_COLOR", category.getColor().getColorValue());

                startActivity(intent);
                overridePendingTransition(0, 0);
            }

            @Override
            public void onEditClick(CategoryItem category, int position) {
                showEditCategoryDialog(category, position);
            }
        });

        recyclerView.setAdapter(adapter);
    }

    public void addCategory(View v) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_category);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        MaterialCardView colorPickerIndicator = dialog.findViewById(R.id.colorPickerIndicator);
        EditText categoryName = dialog.findViewById(R.id.categoryName);
        Button btnDodaj = dialog.findViewById(R.id.btnDodaj);

        final ItemColor[] allColors = ItemColor.values();
        String[] colorNames = new String[allColors.length];
        for (int i = 0; i < allColors.length; i++) {
            colorNames[i] = allColors[i].getDisplayName();
        }

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

                // Fixed duplicate check: we must loop through objects and compare the strings
                for (CategoryItem item : categoryList) {
                    if (item.getName().equalsIgnoreCase(enteredName)) {
                        categoryName.setError("Že obstaja");
                        return;
                    }
                }

                String finalName = enteredName.toUpperCase().charAt(0) + enteredName.substring(1);

                CategoryItem newItem = new CategoryItem(finalName, selectedColor[0]);
                categoryList.add(newItem);

                // 4. Save the updated list to SharedPreferences!
                sessionManager.saveCategories(categoryList);

                if (adapter != null) {
                    adapter.notifyItemInserted(categoryList.size() - 1);
                }
                recyclerView.smoothScrollToPosition(categoryList.size() - 1);

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void showEditCategoryDialog(CategoryItem category, int position) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_category);

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            dialog.getWindow().setLayout(android.view.ViewGroup.LayoutParams.MATCH_PARENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        TextView dodajTitle = dialog.findViewById(R.id.dodajTitle);
        MaterialCardView colorPickerIndicator = dialog.findViewById(R.id.colorPickerIndicator);
        EditText categoryName = dialog.findViewById(R.id.categoryName);
        Button btnDodaj = dialog.findViewById(R.id.btnDodaj);

        // Update UI for editing
        dodajTitle.setText("Uredi kategorijo");
        btnDodaj.setText("Shrani");
        categoryName.setText(category.getName());

        final ItemColor[] allColors = ItemColor.values();
        String[] colorNames = new String[allColors.length];
        for (int i = 0; i < allColors.length; i++) {
            colorNames[i] = allColors[i].getDisplayName();
        }

        final ItemColor[] selectedColor = {category.getColor()};
        colorPickerIndicator.setCardBackgroundColor(selectedColor[0].getColorValue());

        colorPickerIndicator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
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

        btnDodaj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String enteredName = categoryName.getText().toString().trim();

                if (enteredName.isEmpty()) {
                    categoryName.setError("Vnesite ime");
                    return;
                }

                // Check for duplicate names (excluding the current one being edited)
                for (int i = 0; i < categoryList.size(); i++) {
                    if (i != position && categoryList.get(i).getName().equalsIgnoreCase(enteredName)) {
                        categoryName.setError("Že obstaja");
                        return;
                    }
                }

                String oldName = category.getName();
                String finalName = enteredName.toUpperCase().charAt(0) + enteredName.substring(1);

                // Update the category
                category.setName(finalName);
                category.setColor(selectedColor[0]);

                // Also update all items that were in the old category name!
                List<InventoryItem> allItems = sessionManager.getItems();
                boolean itemsUpdated = false;
                for (InventoryItem item : allItems) {
                    if (item.getCategoryName().equals(oldName)) {
                        item.setCategoryName(finalName);
                        itemsUpdated = true;
                    }
                }
                if (itemsUpdated) {
                    sessionManager.saveItems(allItems);
                }

                // Save categories
                sessionManager.saveCategories(categoryList);

                if (adapter != null) {
                    adapter.notifyItemChanged(position);
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    public void resetValues(View v) {
        new AlertDialog.Builder(this)
                .setTitle("Ponastavi količine")
                .setMessage("Ali ste prepričani, da želite ponastaviti količine vseh artiklov na 0?")
                .setPositiveButton("Ponastavi", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<InventoryItem> allItems = sessionManager.getItems();
                        for (InventoryItem item : allItems) {
                            item.setQuantity(0);
                        }
                        sessionManager.saveItems(allItems);
                    }
                })
                .setNegativeButton("Prekliči", null)
                .show();
    }

    public void export(View v) {
        List<InventoryItem> allItems = sessionManager.getItems();

        if (allItems.isEmpty()) {
            new AlertDialog.Builder(this)
                    .setTitle("Izvoz ni mogoč")
                    .setMessage("Nimate artiklov za izvoz.")
                    .setPositiveButton("V redu", null)
                    .show();
            return;
        }

        PdfDocument document = new PdfDocument();
        int pageNumber = 1;
        int currentY;
        int rowHeight = 30;
        int margin = 40;
        int colPadding = 10;
        int pageWidth = 595; // A4
        int pageHeight = 842;

        // Column widths
        int col1Width = (pageWidth - 2 * margin) / 2;
        int col2Width = (pageWidth - 2 * margin) / 4;

        Paint titlePaint = new Paint();
        titlePaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        titlePaint.setTextSize(18);

        Paint headerPaint = new Paint();
        headerPaint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        headerPaint.setTextSize(12);

        Paint textPaint = new Paint();
        textPaint.setTextSize(12);

        Paint zebraPaint = new Paint();
        zebraPaint.setColor(Color.argb(255, 240, 240, 240)); // Very light gray

        Paint linePaint = new Paint();
        linePaint.setColor(Color.LTGRAY);
        linePaint.setStrokeWidth(1);

        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        Canvas canvas = page.getCanvas();

        // Title
        currentY = margin + 30;
        canvas.drawText("Skladko - Inventura", margin, currentY, titlePaint);

        String dateStr = new SimpleDateFormat("dd. MM. yyyy", Locale.getDefault()).format(new Date());
        canvas.drawText("Datum: " + dateStr, margin, currentY + 20, textPaint);

        currentY += 60;

        // Draw Headers
        canvas.drawText("Ime", margin + colPadding, currentY, headerPaint);
        canvas.drawText("Količina", margin + col1Width + colPadding, currentY, headerPaint);
        canvas.drawText("Kategorija", margin + col1Width + col2Width + colPadding, currentY, headerPaint);

        currentY += 10;
        canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint);
        currentY += 20;

        for (int i = 0; i < allItems.size(); i++) {
            InventoryItem item = allItems.get(i);

            // Check if we need a new page
            if (currentY + rowHeight > pageHeight - margin) {
                document.finishPage(page);
                pageNumber++;
                pageInfo = new PdfDocument.PageInfo.Builder(pageWidth, pageHeight, pageNumber).create();
                page = document.startPage(pageInfo);
                canvas = page.getCanvas();
                currentY = margin + 30;
                
                // Redraw headers on new page
                canvas.drawText("Ime", margin + colPadding, currentY, headerPaint);
                canvas.drawText("Količina", margin + col1Width + colPadding, currentY, headerPaint);
                canvas.drawText("Kategorija", margin + col1Width + col2Width + colPadding, currentY, headerPaint);
                currentY += 10;
                canvas.drawLine(margin, currentY, pageWidth - margin, currentY, linePaint);
                currentY += 20;
            }

            // Zebra striping
            if (i % 2 == 1) {
                canvas.drawRect(margin, currentY - 20, pageWidth - margin, currentY + 10, zebraPaint);
            }

            // Draw Item row
            canvas.drawText(item.getName(), margin + colPadding, currentY, textPaint);
            canvas.drawText(item.getDisplayQuantity(), margin + col1Width + colPadding, currentY, textPaint);
            canvas.drawText(item.getCategoryName(), margin + col1Width + col2Width + colPadding, currentY, textPaint);

            // Draw thin line below row
            canvas.drawLine(margin, currentY + 10, pageWidth - margin, currentY + 10, linePaint);

            currentY += rowHeight;
        }

        document.finishPage(page);

        try {
            String dateSuffix = new SimpleDateFormat("d_M_yy", Locale.getDefault()).format(new Date());
            String fileName = "skladko_inventura_" + dateSuffix + ".pdf";

            File cachePath = new File(getCacheDir(), "exports");
            if (!cachePath.exists()) {
                cachePath.mkdirs();
            }
            File pdfFile = new File(cachePath, fileName);
            document.writeTo(new FileOutputStream(pdfFile));
            document.close();

            Uri contentUri = FileProvider.getUriForFile(this, "com.example.skladko.fileprovider", pdfFile);

            new AlertDialog.Builder(this)
                    .setTitle("Izvoz pripravljen")
                    .setMessage("PDF datoteka je bila uspešno ustvarjena.")
                    .setPositiveButton("Deli / Odpri", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(Intent.ACTION_SEND);
                            intent.setType("application/pdf");
                            intent.putExtra(Intent.EXTRA_STREAM, contentUri);
                            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                            startActivity(Intent.createChooser(intent, "Odpri PDF"));
                        }
                    })
                    .setNegativeButton("Zapri", null)
                    .show();

        } catch (IOException e) {
            e.printStackTrace();
            document.close();
            new AlertDialog.Builder(this)
                    .setTitle("Napaka")
                    .setMessage("Prišlo je do napake pri ustvarjanju PDF datoteke.")
                    .setPositiveButton("V redu", null)
                    .show();
        }
    }
}