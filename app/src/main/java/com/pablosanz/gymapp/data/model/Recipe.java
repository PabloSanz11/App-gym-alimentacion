package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipes")
public class Recipe {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private String category;
    private float totalProteinG;
    private float totalCarbsG;
    private float totalCaloriesKcal;
    private float totalFatG;

    public Recipe(String name, String category,
                  float totalProteinG, float totalCarbsG,
                  float totalCaloriesKcal, float totalFatG) {
        this.name = name;
        this.category = category;
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
    public float getTotalProteinG() { return totalProteinG; }
    public void setTotalProteinG(float totalProteinG) { this.totalProteinG = totalProteinG; }
    public float getTotalCarbsG() { return totalCarbsG; }
    public void setTotalCarbsG(float totalCarbsG) { this.totalCarbsG = totalCarbsG; }
    public float getTotalCaloriesKcal() { return totalCaloriesKcal; }
    public void setTotalCaloriesKcal(float totalCaloriesKcal) { this.totalCaloriesKcal = totalCaloriesKcal; }
    public float getTotalFatG() { return totalFatG; }
    public void setTotalFatG(float totalFatG) { this.totalFatG = totalFatG; }
}
