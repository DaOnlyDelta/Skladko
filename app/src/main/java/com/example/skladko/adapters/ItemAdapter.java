package com.example.skladko.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.skladko.R;
import com.example.skladko.models.InventoryItem;
import com.google.android.material.card.MaterialCardView;
import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(InventoryItem item, int position);
    }

    private List<InventoryItem> itemList;
    private OnItemClickListener listener;

    // We removed the categoryColor parameter since each item now knows its own color
    public ItemAdapter(List<InventoryItem> itemList, OnItemClickListener listener) {
        this.itemList = itemList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_box, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        InventoryItem currentItem = itemList.get(position);

        holder.itemNameText.setText(currentItem.getName());
        holder.itemQuantityText.setText(currentItem.getDisplayQuantity());

        // Use the color specifically saved to this item
        holder.itemCard.setCardBackgroundColor(currentItem.getColor().getColorValue());

        holder.itemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int currentPosition = holder.getBindingAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    InventoryItem clickedItem = itemList.get(currentPosition);
                    listener.onItemClick(clickedItem, currentPosition);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView itemNameText;
        TextView itemQuantityText;
        MaterialCardView itemCard;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemNameText = itemView.findViewById(R.id.itemName);
            itemQuantityText = itemView.findViewById(R.id.itemQuantity);
            itemCard = itemView.findViewById(R.id.itemCard);
        }
    }
}