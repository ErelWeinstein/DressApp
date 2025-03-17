package com.example.dressapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ClothingAdapter clothingAdapter;
    private List<ClothingItem> clothingItemList;
    private DatabaseReference wardrobeRef;
    private FirebaseAuth auth;
    private String category;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        clothingItemList = new ArrayList<>();
        clothingAdapter = new ClothingAdapter(this, clothingItemList, position ->
                Toast.makeText(CategoryActivity.this, "Clicked on: " + clothingItemList.get(position).getName(), Toast.LENGTH_SHORT).show()
        );
        recyclerView.setAdapter(clothingAdapter);

        auth = FirebaseAuth.getInstance();

        if (getIntent().hasExtra("category")) {
            category = getIntent().getStringExtra("category");
            loadClothingItems();
        } else {
            Toast.makeText(this, "No category provided", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void loadClothingItems() {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();
        wardrobeRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("wardrobe");

        wardrobeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                clothingItemList.clear();
                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Log.d("FirebaseData", "Raw item: " + itemSnapshot.getValue());

                    String name = itemSnapshot.child("name").getValue(String.class);
                    String type = itemSnapshot.child("type").getValue(String.class);
                    String imageUrl = itemSnapshot.child("imageUrl").getValue(String.class);
                    Object temperatureTag = itemSnapshot.child("temperatureTag").getValue();
                    List<String> temperatureTags = new ArrayList<>();

                    if (temperatureTag instanceof List) {
                        temperatureTags = (List<String>) temperatureTag;
                    } else if (temperatureTag instanceof String) {
                        temperatureTags.add((String) temperatureTag);
                    }
                    Log.d("FirebaseData", "Fetched item: Name = " + name + ", Type = " + type + ", Image = " + imageUrl);

                    if (type != null && type.equalsIgnoreCase(category)) {
                        clothingItemList.add(new ClothingItem(name, type, imageUrl,temperatureTags));
                    }
                }
                clothingAdapter.notifyDataSetChanged();
            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CategoryActivity.this, "Failed to load clothing items.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
