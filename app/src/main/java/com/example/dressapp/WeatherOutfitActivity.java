package com.example.dressapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class WeatherOutfitActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private TextView weatherTextView;
    private FusedLocationProviderClient fusedLocationClient;
    private RecyclerView weatherRecyclerView;
    private ClothingAdapter clothingAdapter;
    private List<ClothingItem> clothingItemList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_outfit);

        weatherTextView = findViewById(R.id.weatherTextView);


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        databaseReference = database.getReference();

        // בדיקת הרשאה וקבלת מיקום
        checkLocationPermission();

        weatherRecyclerView = findViewById(R.id.weatherRecyclerView);
        weatherRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        clothingItemList = new ArrayList<>();
        clothingAdapter = new ClothingAdapter(this, clothingItemList, position -> {
            Toast.makeText(WeatherOutfitActivity.this, "Clicked on: " + clothingItemList.get(position).getName(), Toast.LENGTH_SHORT).show();
        });
        weatherRecyclerView.setAdapter(clothingAdapter);

    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        try {
            fusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Log.d("WeatherOutfitActivity", "Latitude: " + latitude + ", Longitude: " + longitude);

                        // קבלת נתוני מזג האוויר לפי המיקום
                        getWeatherData(latitude, longitude);
                    } else {
                        Log.d("WeatherOutfitActivity", "Location is null, requesting new location...");
                        requestNewLocation();
                    }
                }
            });
        } catch (SecurityException e) {
            Log.e("WeatherOutfitActivity", "SecurityException: " + e.getMessage());
        }
    }

    private void requestNewLocation() {
        LocationRequest locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return; // יוצא מהפונקציה אם אין הרשאה
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, getMainLooper());
    }

    // יצירת משתנה Callback מחוץ לפונקציה
    private final LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            if (locationResult != null && locationResult.getLastLocation() != null) {
                Location location = locationResult.getLastLocation();
                getWeatherData(location.getLatitude(), location.getLongitude());
            }
        }
    };


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocation();
            } else {
                Log.d("WeatherOutfitActivity", "Permission denied!");
            }
        }
    }

    private void getWeatherData(double latitude, double longitude) {
        String apiKey = "de4d63087dd6c364a25fc4d5ec06f9ce";
        String url = "https://api.openweathermap.org/data/2.5/weather?lat=" + latitude + "&lon=" + longitude +
                "&appid=" + apiKey + "&units=metric&lang=he";

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject main = response.getJSONObject("main");
                            double temp = main.getDouble("temp");

                            JSONArray weatherArray = response.getJSONArray("weather");
                            JSONObject weather = weatherArray.getJSONObject(0);
                            String description = weather.getString("description");

                            String weatherText = "טמפרטורה: " + temp + "°C\n" + "מזג אוויר: " + description;
                            weatherTextView.setText(weatherText);
                            loadFilteredOutfits(temp);

                            Log.d("Weather", "Temp: " + temp + "°C, Desc: " + description);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Weather", "Error fetching data: " + error.getMessage());
            }
        });

        queue.add(jsonObjectRequest);
    }

    private void loadFilteredOutfits(double temp) {
        if (auth.getCurrentUser() == null) return;
        String userId = auth.getCurrentUser().getUid();
        DatabaseReference wardrobeRef = databaseReference.child("users").child(userId).child("wardrobe");

        Log.d("Weather", "Loading filtered outfits...");

        wardrobeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("Weather", "Firebase data loaded");
                Log.d("FirebaseTest", "📌 Data snapshot: " + snapshot.getValue());

                clothingItemList.clear();
                Log.d("Weather", "Total clothing items from Firebase: " + snapshot.getChildrenCount());

                for (DataSnapshot itemSnapshot : snapshot.getChildren()) {
                    Log.d("Weather", "Item: " + itemSnapshot.getKey());

                    String name = itemSnapshot.child("name").getValue(String.class);
                    String type = itemSnapshot.child("type").getValue(String.class);
                    String imageUrl = itemSnapshot.child("imageUrl").getValue(String.class);
                    Object temperatureTag = itemSnapshot.child("temperatureTag").getValue();
                    List<String> temperatureTags = new ArrayList<>();

                    Log.d("Weather", "🟢 Raw itemSnapshot: " + itemSnapshot.getValue());

                    if (temperatureTag instanceof List) {
                        temperatureTags.addAll((List<String>) temperatureTag);
                    } else if (temperatureTag instanceof String) {
                        temperatureTags.add((String) temperatureTag);
                    } else if (temperatureTag instanceof Map) {
                        for (Object value : ((Map<?, ?>) temperatureTag).values()) {
                            if (value instanceof String) {
                                temperatureTags.add((String) value);
                            }
                        }
                    }

                    if (imageUrl != null && name != null && type != null) {
                        ClothingItem clothingItem = new ClothingItem(name, type, imageUrl, temperatureTags);
                        Log.d("Weather", "✅ Outfit Loaded: " + clothingItem.getName());

                        if (isOutfitSuitable(clothingItem, temp)) {
                            clothingItemList.add(clothingItem);
                        }
                    } else {
                        Log.e("Weather", "❌ Missing required fields for item: " + itemSnapshot.getKey());
                    }
                }


                Log.d("Weather", "Filtered clothing items count: " + clothingItemList.size());
                clothingAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WeatherOutfitActivity.this, "Failed to load clothing items.", Toast.LENGTH_SHORT).show();
            }


        });
    }



    private boolean isOutfitSuitable(ClothingItem clothingItem, double temp) {
        List<String> temperatureTags = clothingItem.getTemperatureTags(); // עדכון השם בהתאם לשינוי

        Log.d("Weather", "Checking clothing: " + clothingItem.getName() + " for temperature: " + temp);

        if (temperatureTags == null || temperatureTags.isEmpty()) {
            Log.d("Weather", "No temperature tags for clothing " + clothingItem.getName());
            return false;
        }

        for (String tag : temperatureTags) {
            Log.d("Weather", "Checking if clothing is suitable for temperature tag: " + tag);

            if (tag.equals("Cold") && temp < 15) {
                return true;
            } else if (tag.equals("Warm") && temp >= 15 && temp <= 25) {
                return true;
            } else if (tag.equals("Hot") && temp > 25 && temp <= 35) {
                return true;
            } else if (tag.equals("Very Hot") && temp > 35) {
                return true;
            }
        }
        return false;
    }
}
