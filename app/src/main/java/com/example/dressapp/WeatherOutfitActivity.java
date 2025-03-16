package com.example.dressapp;

import android.os.Bundle;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;

public class WeatherOutfitActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_outfit);

        Log.d("WeatherOutfitActivity", "Activity Loaded Successfully!");
    }
}
