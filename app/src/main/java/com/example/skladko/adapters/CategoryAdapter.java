package com.example.skladko.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skladko.models.CategoryItem;
import com.example.skladko.R;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    // 1. Create the interface
    public interface OnCategoryClickListener {
        void onCategoryClick(CategoryItem category);
        void onEditClick(CategoryItem category, int position);
    }

    private List<CategoryItem> categories;
    private OnCategoryClickListener listener; // 2. Add the listener variable

    // 3. Update the constructor to require the listener
    public CategoryAdapter(List<CategoryItem> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
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

        holder.categoryNameText.setText(currentItem.getName());
        holder.categoryCard.setCardBackgroundColor(currentItem.getColor().getColorValue());

        // 4. Attach the click listener to the square
        holder.categoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onCategoryClick(currentItem);
            }
        });

        holder.btnEditCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onEditClick(currentItem, holder.getBindingAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView categoryNameText;
        MaterialCardView categoryCard;
        ImageButton btnEditCategory;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryNameText = itemView.findViewById(R.id.category_name);
            categoryCard = itemView.findViewById(R.id.categoryCard);
            btnEditCategory = itemView.findViewById(R.id.btnEditCategory);
        }
    }
}