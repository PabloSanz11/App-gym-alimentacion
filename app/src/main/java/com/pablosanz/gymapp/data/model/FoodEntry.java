package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "food_entries",
        foreignKeys = @ForeignKey(
                entity = MealLog.class,
                parentColumns = "id",
                childColumns = "mealLogId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("mealLogId")})
public class FoodEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long mealLogId;
    private String foodName;
    private String barcode;
    private float proteinG;
    private float carbsG;
    private float caloriesKcal;
    private float fatG;
    private float quantityG;

    public FoodEntry() {}

    public FoodEntry(long mealLogId, String foodName, String barcode,
                     float proteinG, float carbsG, float caloriesKcal, float fatG, float quantityG) {
        this.mealLogId = mealLogId;
        this.foodName = foodName;
        this.barcode = barcode;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.caloriesKcal = caloriesKcal;
        this.fatG = fatG;
        this.quantityG = quantityG;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getMealLogId() { return mealLogId; }
    public void setMealLogId(long mealLogId) { this.mealLogId = mealLogId; }

    public String getFoodName() { return foodName; }
    public void setFoodName(String foodName) { this.foodName = foodName; }

    public String getBarcode() { return barcode; }
    public void setBarcode(String barcode) { this.barcode = barcode; }

    public float getProteinG() { return proteinG; }
    public void setProteinG(float proteinG) { this.proteinG = proteinG; }

    public float getCarbsG() { return carbsG; }
    public void setCarbsG(float carbsG) { this.carbsG = carbsG; }

    public float getCaloriesKcal() { return caloriesKcal; }
    public void setCaloriesKcal(float caloriesKcal) { this.caloriesKcal = caloriesKcal; }

    public float getFatG() { return fatG; }
    public void setFatG(float fatG) { this.fatG = fatG; }

    public float getQuantityG() { return quantityG; }
    public void setQuantityG(float quantityG) { this.quantityG = quantityG; }
}
