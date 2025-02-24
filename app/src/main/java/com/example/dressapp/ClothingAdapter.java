package com.example.dressapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class ClothingAdapter extends RecyclerView.Adapter<ClothingAdapter.ViewHolder> {

    private Context context;
    private List<ClothingItem> clothingItems;

    public ClothingAdapter(Context context, List<ClothingItem> clothingItems) {
        this.context = context;
        this.clothingItems = clothingItems;
    }

    // עדכון רשימת הבגדים
    public void updateClothingItems(List<ClothingItem> clothingItems) {
        this.clothingItems = clothingItems;
        notifyDataSetChanged();
    }

    // יצירת ה-ViewHolder לכל אייטם ברשימה
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_clothing_item, parent, false);
        return new ViewHolder(view);
    }

    // קשירת נתונים ל-View
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ClothingItem item = clothingItems.get(position);

        // הצגת שם פריט הבגד
        holder.clothingName.setText(item.getName());

        // הצגת סוג הבגד
        holder.clothingType.setText(item.getType());

        // הצגת התמונה אם יש URL
        Glide.with(context).load(item.getImageUrl()).into(holder.clothingImage);
    }

    @Override
    public int getItemCount() {
        return clothingItems.size();
    }

    // מחלקת ViewHolder עבור כל אייטם
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView clothingName;
        TextView clothingType;
        ImageView clothingImage;  // ImageView להצגת תמונת הבגד

        public ViewHolder(View itemView) {
            super(itemView);
            clothingName = itemView.findViewById(R.id.clothingName);  // הנחה שיש לך TextView בשם clothingName בעיצוב
            clothingType = itemView.findViewById(R.id.clothingType);  // הנחה שיש לך TextView בשם clothingType בעיצוב
            clothingImage = itemView.findViewById(R.id.clothingImage);  // הנחה שיש לך ImageView בשם clothingImage בעיצוב
        }
    }
}
