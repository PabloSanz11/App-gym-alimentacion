package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

/** Snapshot de un ingrediente para una entrada de comida específica de un día.
 *  A diferencia de RecipeIngredient (plantilla compartida de la receta), estas filas
 *  pertenecen a un FoodEntry puntual y se pueden editar/agregar/eliminar sin afectar
 *  la receta original ni otros días en los que se haya registrado la misma receta. */
@Entity(tableName = "food_entry_ingredients",
        foreignKeys = @ForeignKey(
                entity = FoodEntry.class,
                parentColumns = "id",
                childColumns = "foodEntryId",
                onDelete = ForeignKey.CASCADE),
        indices = @Index("foodEntryId"))
public class FoodEntryIngredient {

    @PrimaryKey(autoGenerate = true)
    private long id;
    private long foodEntryId;
    private String ingredientName;
    private float quantityG;
    private float proteinG;
    private float carbsG;
    private float caloriesKcal;
    private float fatG;

    public FoodEntryIngredient(long foodEntryId, String ingredientName, float quantityG,
                               float proteinG, float carbsG, float caloriesKcal, float fatG) {
        this.foodEntryId = foodEntryId;
        this.ingredientName = ingredientName;
        this.quantityG = quantityG;
        this.proteinG = proteinG;
        this.carbsG = carbsG;
        this.caloriesKcal = caloriesKcal;
        this.fatG = fatG;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public long getFoodEntryId() { return foodEntryId; }
    public void setFoodEntryId(long foodEntryId) { this.foodEntryId = foodEntryId; }
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
