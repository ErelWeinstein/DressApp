package com.example.dressapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private DatabaseReference myRef;

    // Views for clothing item input
    private EditText clothingNameEditText;
    private EditText clothingTypeEditText;

    Button buttonShirts, buttonPants, buttonDresses, buttonJackets;
    LinearLayout clothingContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // חיבור כפתורים ל-XML
        buttonShirts = findViewById(R.id.button_shirts);
        buttonPants = findViewById(R.id.button_pants);
        buttonDresses = findViewById(R.id.button_dresses);
        buttonJackets = findViewById(R.id.button_jackets);

        // חיבור ללחיצות כפתורים - כל כפתור יוביל לעמוד המתאים
        buttonShirts.setOnClickListener(v -> openShirtsActivity());
        buttonPants.setOnClickListener(v -> openPantsActivity());
        buttonDresses.setOnClickListener(v -> openDressesActivity());
        buttonJackets.setOnClickListener(v -> openJacketsActivity());

        // הכנה ל-Firebase
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

        // חיבור ל-Views
        ImageView profileImageView = findViewById(R.id.profileImage);
        TextView nameTextView = findViewById(R.id.nameTv);
        TextView emailTextView = findViewById(R.id.mailTv);

        // Clothing input fields
        clothingNameEditText = findViewById(R.id.clothingNameEditText);
        clothingTypeEditText = findViewById(R.id.clothingTypeEditText);

        // בדיקה אם המשתמש מחובר והצגת הפרטים שלו
        if (auth.getCurrentUser() != null) {
            String name = auth.getCurrentUser().getDisplayName();
            String email = auth.getCurrentUser().getEmail();
            String photoUrl = auth.getCurrentUser().getPhotoUrl() != null
                    ? auth.getCurrentUser().getPhotoUrl().toString()
                    : ""; // תמונה ברירת מחדל במקרה שאין תמונה

            nameTextView.setText(name != null ? name : "No Name Available");
            emailTextView.setText(email != null ? email : "No email available");
            Glide.with(this).load(photoUrl).into(profileImageView);
        } else {
            // אם אין משתמש מחובר, מציגים פרטי ברירת מחדל
            nameTextView.setText("Guest");
            emailTextView.setText("No email available");
            Glide.with(this).load("https://www.example.com/default-profile-image.png") // תמונת פרופיל ברירת מחדל
                    .into(profileImageView);
        }

        // חיבור לכפתור והוספת Listener
        Button addClothingButton = findViewById(R.id.addClothingButton);
        addClothingButton.setOnClickListener(v -> addClothingItem()); // קריאה לפונקציה להוספת פריט
    }

    // פונקציה להוספת פריט למסד הנתונים
    private void addClothingItem() {
        if (auth.getCurrentUser() != null) {
            // קבלת קלט מה-EditText
            String clothingName = clothingNameEditText.getText().toString().trim();
            String clothingType = clothingTypeEditText.getText().toString().trim();
            String imageUrl = "https://example.com/default-image-url.png"; // כתובת תמונה ברירת מחדל

            // בדיקת קלט
            if (clothingName.isEmpty() || clothingType.isEmpty()) {
                Toast.makeText(this, "Please enter all details!", Toast.LENGTH_SHORT).show();
                return;
            }

            // יצירת מסלול לנתוני המשתמש והארון
            String userId = auth.getCurrentUser().getUid();
            DatabaseReference userWardrobeRef = myRef.child(userId).child("wardrobe").push();

            // שמירת הפרטים למסד הנתונים
            userWardrobeRef.child("name").setValue(clothingName);
            userWardrobeRef.child("type").setValue(clothingType);
            userWardrobeRef.child("imageUrl").setValue(imageUrl);

            // הודעה על הצלחה
            Toast.makeText(this, "Clothing item added successfully!", Toast.LENGTH_SHORT).show();

            // ניקוי השדות לאחר ההוספה
            clothingNameEditText.setText("");
            clothingTypeEditText.setText("");
        } else {
            // אם אין משתמש מחובר
            Toast.makeText(this, "Please log in to add clothing items.", Toast.LENGTH_SHORT).show();
        }
    }

    // פתיחת פעילויות שונות עבור כל קטגוריה
    private void openShirtsActivity() {
        Intent intent = new Intent(ProfileActivity.this, ShirtsActivity.class);
        startActivity(intent);
    }

    private void openPantsActivity() {
        Intent intent = new Intent(ProfileActivity.this, PantsActivity.class);
        startActivity(intent);
    }

    private void openDressesActivity() {
        Intent intent = new Intent(ProfileActivity.this, SkirtsDressesActivity.class);
        startActivity(intent);
    }

    private void openJacketsActivity() {
        Intent intent = new Intent(ProfileActivity.this, JacketsSweatersActivity.class);
        startActivity(intent);
    }
}
