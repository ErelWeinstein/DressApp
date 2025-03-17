package com.example.dressapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    private RecyclerView recyclerView;
    private ClothingAdapter clothingAdapter;
    private List<ClothingItem> clothingItemList;

    Button buttonShirts, buttonPants, buttonDresses, buttonJackets;
    private String category;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // חיבור ל-XML
        buttonShirts = findViewById(R.id.button_shirts);
        buttonPants = findViewById(R.id.button_pants);
        buttonDresses = findViewById(R.id.button_dresses);
        buttonJackets = findViewById(R.id.button_jackets);

        // חיבור ל-Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        // חיבור ל-Views בפרופיל
        ImageView profileImageView = findViewById(R.id.profileImage);
        TextView nameTextView = findViewById(R.id.nameTv);
        TextView emailTextView = findViewById(R.id.mailTv);

        // הגדרת RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        clothingItemList = new ArrayList<>();
        clothingAdapter = new ClothingAdapter(this, clothingItemList, position -> {
            Toast.makeText(ProfileActivity.this, "Clicked on: " + clothingItemList.get(position).getName(), Toast.LENGTH_SHORT).show();
        });

        recyclerView.setAdapter(clothingAdapter);

        // בדיקה אם המשתמש מחובר
        if (auth.getCurrentUser() != null) {
            String name = auth.getCurrentUser().getDisplayName();
            String email = auth.getCurrentUser().getEmail();
            String photoUrl = auth.getCurrentUser().getPhotoUrl() != null ? auth.getCurrentUser().getPhotoUrl().toString() : "";

            nameTextView.setText(name != null ? name : "No Name Available");
            emailTextView.setText(email != null ? email : "No email available");
            Glide.with(this).load(photoUrl).into(profileImageView);

            // טעינת הבגדים מה-Firebase
            loadClothingItems();
        } else {
            nameTextView.setText("Guest");
            emailTextView.setText("No email available");
            Glide.with(this).load("https://www.example.com/default-profile-image.png").into(profileImageView);
        }

        // הוספת מאזיני לחיצה לכפתורים כדי להציג את הבגדים לפי קטגוריה
        buttonShirts.setOnClickListener(v -> openCategoryActivity("Shirts"));
        buttonPants.setOnClickListener(v -> openCategoryActivity("Pants"));
        buttonDresses.setOnClickListener(v -> openCategoryActivity("Dresses"));
        buttonJackets.setOnClickListener(v -> openCategoryActivity("Jackets"));


    }

    private void loadClothingItems() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference wardrobeRef = myRef.child(userId).child("wardrobe");

        wardrobeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clothingItemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    String name = itemSnapshot.child("name").getValue(String.class);
                    String type = itemSnapshot.child("type").getValue(String.class);
                    String imageUrl = itemSnapshot.child("imageUrl").getValue(String.class);
                    List<String> temperatureTag = new ArrayList<>();
                    for (DataSnapshot tempTagSnapshot : itemSnapshot.child("temperatureTag").getChildren()) {
                        temperatureTag.add(tempTagSnapshot.getValue(String.class));
                    }

                    if (type != null && type.equals(category)) {
                        clothingItemList.add(new ClothingItem(name, type, imageUrl, temperatureTag));
                    }

                }

                // בדיקה אם הנתונים נכנסים לרשימה
                Log.d("ItemListSize", "Size of clothingItemList: " + clothingItemList.size());

                clothingAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error loading clothing items: " + error.getMessage());
                Toast.makeText(ProfileActivity.this, "Failed to load clothing items.", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void filterClothingByCategory(String category) {
        List<ClothingItem> filteredList = new ArrayList<>();
        for (ClothingItem item : clothingItemList) {
            if (item.getType().equals(category)) { // בודק אם הפריט שייך לקטגוריה שנבחרה
                filteredList.add(item);
            }
        }
        clothingAdapter.updateClothingItems(filteredList); //מעדכן את הרשימה ב-RecyclerView
    }

    private void openCategoryActivity(String category) {
        Log.d("CategoryCheck", "Opening CategoryActivity with category: " + category); // בדיקה אם הקטגוריה מועברת נכון
        Intent intent = new Intent(ProfileActivity.this, CategoryActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }


}
