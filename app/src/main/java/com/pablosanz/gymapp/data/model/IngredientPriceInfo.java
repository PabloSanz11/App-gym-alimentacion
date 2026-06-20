package com.pablosanz.gymapp.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Remembers, per ingredient, the last price the user entered and which supermarket they
 * bought it at — survives "Limpiar semana" and list regeneration so future shopping lists
 * can be pre-filled automatically.
 */
@Entity(tableName = "ingredient_price_info")
public class IngredientPriceInfo {

    @PrimaryKey
    @NonNull
    private String ingredientName = "";

    private float estimatedCostMxn;
    private String store;

    public IngredientPriceInfo(@NonNull String ingredientName, float estimatedCostMxn, String store) {
        this.ingredientName = ingredientName;
        this.estimatedCostMxn = estimatedCostMxn;
        this.store = store;
    }

    @NonNull
    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(@NonNull String ingredientName) { this.ingredientName = ingredientName; }
    public float getEstimatedCostMxn() { return estimatedCostMxn; }
    public void setEstimatedCostMxn(float estimatedCostMxn) { this.estimatedCostMxn = estimatedCostMxn; }
    public String getStore() { return store; }
    public void setStore(String store) { this.store = store; }
}
