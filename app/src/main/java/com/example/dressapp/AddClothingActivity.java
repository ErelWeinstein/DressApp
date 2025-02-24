package com.example.dressapp;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class AddClothingActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference wardrobeRef;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);

        // אתחול Firebase
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        wardrobeRef = database.getReference("users").child(auth.getCurrentUser().getUid()).child("wardrobe");

        // בדוק אם המשתמש מחובר
        if (auth.getCurrentUser() != null) {
            Log.d("UserStatus", "User is logged in: " + auth.getCurrentUser().getUid());
        } else {
            Log.d("UserStatus", "No user is logged in");
        }

        // קישור בין ה-Views לקוד
        EditText typeInput = findViewById(R.id.typeInput);
        EditText colorInput = findViewById(R.id.colorInput);
        EditText seasonInput = findViewById(R.id.seasonInput);
        EditText notesInput = findViewById(R.id.notesInput);
        Button saveButton = findViewById(R.id.saveButton);

        // אירוע לחיצה על כפתור "שמירה"
        saveButton.setOnClickListener(view -> {
            String type = typeInput.getText().toString();
            String color = colorInput.getText().toString();
            String season = seasonInput.getText().toString();
            String notes = notesInput.getText().toString();

            // יצירת המידע שיישמר במסד הנתונים
            String clothingId = wardrobeRef.push().getKey();  // יצירת מזהה ייחודי למוצר ב-wardrobe
            Map<String, Object> clothingItem = new HashMap<>();
            clothingItem.put("type", type);
            clothingItem.put("color", color);
            clothingItem.put("season", season);
            clothingItem.put("notes", notes);

            // שמירת המידע ב-Realtime Database
            if (clothingId != null) {
                wardrobeRef.child(clothingId).setValue(clothingItem)
                        .addOnSuccessListener(aVoid -> {
                            Log.d("Firebase", "Data written successfully: " + clothingItem);
                            finish(); // סיום המסך לאחר שמירה
                        })
                        .addOnFailureListener(e -> {
                            Log.e("Firebase", "Error writing data: " + e.getMessage());
                        });
            }
        });
    }
}
