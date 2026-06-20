package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "shopping_list_items")
public class ShoppingListItem {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String ingredientName;
    private float totalQuantityG;
    private boolean purchased;
    /** Costo estimado en MXN, editable por el usuario para ajustarlo al precio real. */
    private float estimatedCostMxn;

    public ShoppingListItem(String ingredientName, float totalQuantityG, boolean purchased) {
        this(ingredientName, totalQuantityG, purchased, 0f);
    }

    public ShoppingListItem(String ingredientName, float totalQuantityG, boolean purchased, float estimatedCostMxn) {
        this.ingredientName = ingredientName;
        this.totalQuantityG = totalQuantityG;
        this.purchased = purchased;
        this.estimatedCostMxn = estimatedCostMxn;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getIngredientName() { return ingredientName; }
    public void setIngredientName(String ingredientName) { this.ingredientName = ingredientName; }
    public float getTotalQuantityG() { return totalQuantityG; }
    public void setTotalQuantityG(float totalQuantityG) { this.totalQuantityG = totalQuantityG; }
    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }
    public float getEstimatedCostMxn() { return estimatedCostMxn; }
    public void setEstimatedCostMxn(float estimatedCostMxn) { this.estimatedCostMxn = estimatedCostMxn; }
}
