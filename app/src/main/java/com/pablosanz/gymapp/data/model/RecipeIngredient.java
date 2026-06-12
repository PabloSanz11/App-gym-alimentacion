package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_ingredients",
        foreignKeys = @ForeignKey(
                entity = Recipe.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("recipeId"))
public class RecipeIngredient {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long recipeId;
    private String ingredientName;
    private float quantityG;
    private float proteinG;
    private float carbsG;
    private float caloriesKcal;
    private float fatG;

    public RecipeIngredient(long recipeId, String ingredientName, float quantityG,
                            float proteinG, float carbsG, float caloriesKcal, float fatG) {
        this.recipeId = recipeId;
        this.ingredientName = ingredientName;
        this.quantityG = quantityG;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.caloriesKcal = caloriesKcal;
        this.fatG = fatG;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getRecipeId() { return recipeId; }
    public void setRecipeId(long recipeId) { this.recipeId = recipeId; }
    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }
    public float getQuantityG() { return quantityG; }
    public void setQuantityG(float quantityG) { this.quantityG = quantityG; }
    public float getProteinG() { return proteinG; }
    public void setProteinG(float proteinG) { this.proteinG = proteinG; }
    public float getCarbsG() { return carbsG; }
    public void setCarbsG(float carbsG) { this.carbsG = carbsG; }
    public float getCaloriesKcal() { return caloriesKcal; }
    public void setCaloriesKcal(float caloriesKcal) { this.caloriesKcal = caloriesKcal; }
    public float getFatG() { return fatG; }
    public void setFatG(float fatG) { this.fatG = fatG; }
}
