package com.example.dressapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddClothingActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference wardrobeRef;
    private FirebaseAuth auth;
    private FirebaseStorage storage;
    private StorageReference storageRef;

    private EditText typeInput, colorInput, seasonInput, notesInput;
    private ImageView clothingImageView;
    private Button selectImageButton, saveButton;

    private Uri selectedImageUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_clothing);

        // אתחול Firebase
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        wardrobeRef = database.getReference("users").child(auth.getCurrentUser().getUid()).child("wardrobe");

        // בדיקת התחברות
        if (auth.getCurrentUser() != null) {
            Log.d("UserStatus", "User is logged in: " + auth.getCurrentUser().getUid());
        } else {
            Log.d("UserStatus", "No user is logged in");
        }

        // חיבור Views
        typeInput = findViewById(R.id.typeInput);
        colorInput = findViewById(R.id.colorInput);
        seasonInput = findViewById(R.id.seasonInput);
        notesInput = findViewById(R.id.notesInput);
        clothingImageView = findViewById(R.id.clothingImageView);
        selectImageButton = findViewById(R.id.selectImageButton);
        saveButton = findViewById(R.id.saveButton);

        // לחיצה על "בחר תמונה" לפתיחת הגלריה
        selectImageButton.setOnClickListener(view -> openGallery());

        // לחיצה על "שמירה" תעלה את התמונה (אם נבחרה) ותשמור נתונים
        saveButton.setOnClickListener(view -> saveClothingItem());
    }

    // פתיחת הגלריה לבחירת תמונה
    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        galleryLauncher.launch(intent);
    }

    // מקבל את התמונה מהגלריה
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    clothingImageView.setImageURI(selectedImageUri);
                }
            }
    );

    // שמירת פריט הלבוש
    private void saveClothingItem() {
        String type = typeInput.getText().toString();
        String color = colorInput.getText().toString();
        String season = seasonInput.getText().toString();
        String notes = notesInput.getText().toString();

        if (type.isEmpty() || color.isEmpty() || season.isEmpty()) {
            Toast.makeText(this, "נא למלא את כל השדות", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedImageUri != null) {
            uploadImageToFirebaseStorage(type, color, season, notes);
        } else {
            saveToDatabase(type, color, season, notes, null);
        }
    }

    // העלאת תמונה ל-Firebase Storage
    private void uploadImageToFirebaseStorage(String type, String color, String season, String notes) {
        String imageName = UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child("clothing_images/" + imageName);

        imageRef.putFile(selectedImageUri)
                .addOnSuccessListener(taskSnapshot -> imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    saveToDatabase(type, color, season, notes, uri.toString());
                }))
                .addOnFailureListener(e -> {
                    Log.e("Firebase Storage", "Error uploading image: " + e.getMessage());
                    Toast.makeText(this, "שגיאה בהעלאת התמונה", Toast.LENGTH_SHORT).show();
                });
    }

    // שמירת נתונים ל-Firebase Realtime Database
    private void saveToDatabase(String type, String color, String season, String notes, String imageUrl) {
        String clothingId = wardrobeRef.push().getKey();
        Map<String, Object> clothingItem = new HashMap<>();
        clothingItem.put("type", type);
        clothingItem.put("color", color);
        clothingItem.put("season", season);
        clothingItem.put("notes", notes);
        if (imageUrl != null) {
            clothingItem.put("imageUrl", imageUrl);
        }

        if (clothingId != null) {
            wardrobeRef.child(clothingId).setValue(clothingItem)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firebase", "Data written successfully: " + clothingItem);
                        Toast.makeText(this, "פריט נשמר בהצלחה!", Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firebase", "Error writing data: " + e.getMessage());
                    });
        }
    }
}
