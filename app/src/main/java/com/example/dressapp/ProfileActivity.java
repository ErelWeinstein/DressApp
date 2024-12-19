package com.example.dressapp;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;
    private ClothingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // הכנה ל-Firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // חיבור לו Views
        ImageView profileImageView = findViewById(R.id.profileImage);
        TextView nameTextView = findViewById(R.id.nameTv);
        TextView emailTextView = findViewById(R.id.mailTv);

        // הצגת פרטי המשתמש
        String name = auth.getCurrentUser().getDisplayName();
        String email = auth.getCurrentUser().getEmail();
        String photoUrl = auth.getCurrentUser().getPhotoUrl().toString();

        nameTextView.setText(name);
        emailTextView.setText(email);
        Glide.with(this).load(photoUrl).into(profileImageView);

        // הצגת פריטי הארון
        recyclerView = findViewById(R.id.clothingRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ClothingAdapter(this, new ArrayList<>());
        recyclerView.setAdapter(adapter);

        // לשלוף את פרטי הארון מ-Firestore
        db.collection("users").document(auth.getCurrentUser().getUid()).collection("clothing")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<ClothingItem> items = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            ClothingItem item = document.toObject(ClothingItem.class);
                            items.add(item);
                        }
                        // עדכון ה-Adapter עם פריטי הארון
                        adapter.updateClothingItems(items);
                    }
                });
    }
}
