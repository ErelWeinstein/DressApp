package com.example.dressapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private Context context;
    private List<ClothingItem> clothingItems;
    private OnImageClickListener onImageClickListener;

    public interface OnImageClickListener {
        void onImageClick(int position);
    }

    public ClothingAdapter(Context context, List<ClothingItem> clothingItems, OnImageClickListener listener) {
        this.context = context;
        this.clothingItems = clothingItems;
        this.onImageClickListener = listener; // שמירת המאזין
    }

    // עדכון רשימת הבגדים בצורה נכונה
    public void updateClothingItems(List<ClothingItem> newList) {
        this.clothingItems = newList; // מעדכן את הרשימה
        notifyDataSetChanged(); // רענון התצוגה
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_clothing_item, parent, false);
        return new ViewHolder(view, onImageClickListener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClothingItem item = clothingItems.get(position);

        // הצגת שם פריט הבגד
        holder.clothingName.setText(item.getName());

        // הצגת סוג הבגד
        holder.clothingType.setText(item.getType());

        // הצגת תמונת הבגד עם Glide
        Glide.with(context)
                .load(item.getImageUrl())
                .into(holder.clothingImage);
    }

    @Override
    public int getItemCount() {
        return clothingItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView clothingName;
        TextView clothingType;
        ImageView clothingImage;

        public ViewHolder(@NonNull View itemView, final OnImageClickListener listener) {
            super(itemView);
            clothingName = itemView.findViewById(R.id.clothingName);
            clothingType = itemView.findViewById(R.id.clothingType);
            clothingImage = itemView.findViewById(R.id.clothingImage);

            // מאזין ללחיצה על התמונה
            clothingImage.setOnClickListener(v -> {
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onImageClick(position);
                    }
                }
            });
        }
    }
}
