package com.example.skladko;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // Now accepting our custom object instead of strings
    private List<CategoryItem> categories;

    public CategoryAdapter(List<CategoryItem> categories) {
        this.categories = categories;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_box, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        CategoryItem currentItem = categories.get(position);

        // 1. Set the text
        holder.categoryNameText.setText(currentItem.getName());

        // 2. Set the color by calling getColorValue() on your Enum
        holder.categoryCard.setCardBackgroundColor(currentItem.getColor().getColorValue());
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameText;
        MaterialCardView categoryCard;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameText = itemView.findViewById(R.id.category_name);
            // This is why we added the ID to the XML!
            categoryCard = itemView.findViewById(R.id.categoryCard);
        }
    }
}