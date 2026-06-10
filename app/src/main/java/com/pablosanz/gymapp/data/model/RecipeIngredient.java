package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "recipe_ingredients")
public class RecipeIngredient {
    @PrimaryKey(autoGenerate = true)
    private long id;
    private long recipeId;
    private String name;
    private float quantityG;
    private float caloriesKcal;
    private float proteinG;
    private float carbsG;
    private float fatG;
    private boolean included; // default true, user can uncheck
    private String alternativeNote; // e.g. "Puedes usar totopos horneados"

    public RecipeIngredient() {}
    public RecipeIngredient(long recipeId, String name, float quantityG,
                            float caloriesKcal, float proteinG, float carbsG, float fatG,
                            boolean included, String alternativeNote) {
        this.recipeId = recipeId; this.name = name; this.quantityG = quantityG;
        this.caloriesKcal = caloriesKcal; this.proteinG = proteinG;
        this.carbsG = carbsG; this.fatG = fatG;
        this.included = included; this.alternativeNote = alternativeNote;
    }
    // all getters and setters
    public long getId() { return id; } public void setId(long id) { this.id = id; }
    public long getRecipeId() { return recipeId; } public void setRecipeId(long recipeId) { this.recipeId = recipeId; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public float getQuantityG() { return quantityG; } public void setQuantityG(float q) { this.quantityG = q; }
    public float getCaloriesKcal() { return caloriesKcal; } public void setCaloriesKcal(float c) { this.caloriesKcal = c; }
    public float getProteinG() { return proteinG; } public void setProteinG(float p) { this.proteinG = p; }
    public float getCarbsG() { return carbsG; } public void setCarbsG(float c) { this.carbsG = c; }
    public float getFatG() { return fatG; } public void setFatG(float f) { this.fatG = f; }
    public boolean isIncluded() { return included; } public void setIncluded(boolean included) { this.included = included; }
    public String getAlternativeNote() { return alternativeNote; } public void setAlternativeNote(String s) { this.alternativeNote = s; }
}
