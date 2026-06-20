package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "favorite_foods")
public class FavoriteFood {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private String name;
    private float proteinG;
    private float carbsG;
    private float caloriesKcal;
    private float fatG;
    private float defaultQuantityG;
    /** "desayuno" | "almuerzo" | "merienda" | "cena" | null (disponible para cualquier comida). */
    private String mealSlot;

    public FavoriteFood() {}

    public FavoriteFood(String name, float proteinG, float carbsG, float caloriesKcal, float fatG, float defaultQuantityG) {
        this(name, proteinG, carbsG, caloriesKcal, fatG, defaultQuantityG, null);
    }

    public FavoriteFood(String name, float proteinG, float carbsG, float caloriesKcal, float fatG, float defaultQuantityG, String mealSlot) {
        this.name = name;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.caloriesKcal = caloriesKcal;
        this.fatG = fatG;
        this.defaultQuantityG = defaultQuantityG;
        this.mealSlot = mealSlot;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public float getProteinG() { return proteinG; }
    public void setProteinG(float proteinG) { this.proteinG = proteinG; }
    public float getCarbsG() { return carbsG; }
    public void setCarbsG(float carbsG) { this.carbsG = carbsG; }
    public float getCaloriesKcal() { return caloriesKcal; }
    public void setCaloriesKcal(float caloriesKcal) { this.caloriesKcal = caloriesKcal; }
    public float getFatG() { return fatG; }
    public void setFatG(float fatG) { this.fatG = fatG; }
    public float getDefaultQuantityG() { return defaultQuantityG; }
    public void setDefaultQuantityG(float defaultQuantityG) { this.defaultQuantityG = defaultQuantityG; }
    public String getMealSlot() { return mealSlot; }
    public void setMealSlot(String mealSlot) { this.mealSlot = mealSlot; }
}
