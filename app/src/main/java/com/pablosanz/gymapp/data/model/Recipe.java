package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String category;
    private String description;
    private String imageEmoji;
    private float totalProteinG;
    private float totalCarbsG;
    private float totalCaloriesKcal;
    private float totalFatG;

    public Recipe(String name, String category, String description, String imageEmoji,
                  float totalProteinG, float totalCarbsG,
                  float totalCaloriesKcal, float totalFatG) {
        this.name = name;
        this.category = category;
        this.description = description;
        this.imageEmoji = imageEmoji;
        this.totalProteinG = totalProteinG;
        this.totalCarbsG = totalCarbsG;
        this.totalCaloriesKcal = totalCaloriesKcal;
        this.totalFatG = totalFatG;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getImageEmoji() { return imageEmoji; }
    public void setImageEmoji(String imageEmoji) { this.imageEmoji = imageEmoji; }
    public float getTotalProteinG() { return totalProteinG; }
    public void setTotalProteinG(float v) { this.totalProteinG = v; }
    public float getTotalCarbsG() { return totalCarbsG; }
    public void setTotalCarbsG(float v) { this.totalCarbsG = v; }
    public float getTotalCaloriesKcal() { return totalCaloriesKcal; }
    public void setTotalCaloriesKcal(float v) { this.totalCaloriesKcal = v; }
    public float getTotalFatG() { return totalFatG; }
    public void setTotalFatG(float v) { this.totalFatG = v; }
}
