package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_logs")
public class MealLog {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String date; // "yyyy-MM-dd"
    private String mealSlot; // "desayuno", "almuerzo", "merienda", "cena"
    private float totalProteinG;
    private float totalCarbsG;
    private float totalCaloriesKcal;
    private float totalFatG;

    public MealLog() {}

    public MealLog(String date, String mealSlot) {
        this.date = date;
        this.mealSlot = mealSlot;
        this.totalProteinG = 0;
        this.totalCarbsG = 0;
        this.totalCaloriesKcal = 0;
        this.totalFatG = 0;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getMealSlot() { return mealSlot; }
    public void setMealSlot(String mealSlot) { this.mealSlot = mealSlot; }

    public float getTotalProteinG() { return totalProteinG; }
    public void setTotalProteinG(float totalProteinG) { this.totalProteinG = totalProteinG; }

    public float getTotalCarbsG() { return totalCarbsG; }
    public void setTotalCarbsG(float totalCarbsG) { this.totalCarbsG = totalCarbsG; }

    public float getTotalCaloriesKcal() { return totalCaloriesKcal; }
    public void setTotalCaloriesKcal(float totalCaloriesKcal) { this.totalCaloriesKcal = totalCaloriesKcal; }

    public float getTotalFatG() { return totalFatG; }
    public void setTotalFatG(float totalFatG) { this.totalFatG = totalFatG; }
}
