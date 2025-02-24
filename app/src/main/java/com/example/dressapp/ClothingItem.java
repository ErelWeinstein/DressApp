package com.example.dressapp;

public class ClothingItem {
    private String name;
    private String type;
    private String imageUrl;  // שדה לתמונה

    public ClothingItem() {}

    public ClothingItem(String name, String type, String imageUrl) {
        this.name = name;
        this.type = type;
        this.imageUrl = imageUrl;  // הגדרת התמונה
    }

    // Accessor methods
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
