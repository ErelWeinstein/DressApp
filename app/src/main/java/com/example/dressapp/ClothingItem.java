package com.example.dressapp;

import java.util.ArrayList;
import java.util.List;

public class ClothingItem {
    private String imageUrl;
    private String name;
    private List<String> temperatureTag; // עכשיו זו רשימה של מחרוזות
    private String type;

    public ClothingItem() {
        // נדרש לפיירבייס
    }

    public ClothingItem(String name, String type, String imageUrl,List<String> temperatureTag) {
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;
        this.temperatureTag = temperatureTag != null ? temperatureTag : new ArrayList<>();
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getName() {
        return name;
    }

    public List<String> getTemperatureTags() {
        return temperatureTag != null ? temperatureTag : new ArrayList<>();
    }

    public String getType() {
        return type;
    }
}
