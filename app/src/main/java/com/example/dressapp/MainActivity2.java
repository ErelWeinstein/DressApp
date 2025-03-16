package com.example.dressapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // הכפתורים במסך הראשי
        Button btnOutfitDiary = findViewById(R.id.btn_outfit_diary);
        Button btnOutfitCreation = findViewById(R.id.btn_weather_outfit);
        Button btnProfile = findViewById(R.id.btn_profile);

        // כאשר המשתמש לוחץ על "Outfit Diary"
        btnOutfitDiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // עובר למסך של Outfit Diary
                startActivity(new Intent(MainActivity2.this, OutfitDiaryActivity.class));
            }
        });

        // כאשר המשתמש לוחץ על "Outfit Creation"
        btnOutfitCreation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // עובר למסך של Outfit Creation
                startActivity(new Intent(MainActivity2.this, WeatherOutfitActivity.class));
            }
        });

        // כאשר המשתמש לוחץ על "Profile"
        btnProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // עובר למסך של Profile
                startActivity(new Intent(MainActivity2.this, ProfileActivity.class));
            }
        });
    }
}
