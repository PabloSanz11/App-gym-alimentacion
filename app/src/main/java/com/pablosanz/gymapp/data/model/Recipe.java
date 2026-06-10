package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String description;
    private String category; // "desayuno", "comida", "cena", "cualquiera"
    private String imageEmoji; // e.g. "🌮"

    public Recipe() {}
    public Recipe(String name, String description, String category, String imageEmoji) {
        this.name = name; this.description = description;
        this.category = category; this.imageEmoji = imageEmoji;
    }
    // getters and setters for all fields
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getImageEmoji() { return imageEmoji; }
    public void setImageEmoji(String imageEmoji) { this.imageEmoji = imageEmoji; }
}
