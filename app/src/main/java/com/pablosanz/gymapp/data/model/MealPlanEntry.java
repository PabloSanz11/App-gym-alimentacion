package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meal_plan_entries")
public class MealPlanEntry {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String dayOfWeek;
    private String mealSlot;
    private long recipeId;
    private String recipeName;
    /** -1 si la celda está asignada a una receta en vez de un favorito. */
    private long favoriteFoodId = -1;
    private String favoriteFoodName;

    public MealPlanEntry(String dayOfWeek, String mealSlot, long recipeId, String recipeName) {
        this.dayOfWeek = dayOfWeek;
        this.mealSlot = mealSlot;
        this.recipeId = recipeId;
        this.recipeName = recipeName;
    }

    public static MealPlanEntry forFavorite(String dayOfWeek, String mealSlot, long favoriteFoodId, String favoriteFoodName) {
        MealPlanEntry e = new MealPlanEntry(dayOfWeek, mealSlot, -1, null);
        e.favoriteFoodId = favoriteFoodId;
        e.favoriteFoodName = favoriteFoodName;
        return e;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getDayOfWeek() { return dayOfWeek; }
    public void setDayOfWeek(String dayOfWeek) { this.dayOfWeek = dayOfWeek; }
    public String getMealSlot() { return mealSlot; }
    public void setMealSlot(String mealSlot) { this.mealSlot = mealSlot; }
    public long getRecipeId() { return recipeId; }
    public void setRecipeId(long recipeId) { this.recipeId = recipeId; }
    public String getRecipeName() { return recipeName; }
    public void setRecipeName(String recipeName) { this.recipeName = recipeName; }
    public long getFavoriteFoodId() { return favoriteFoodId; }
    public void setFavoriteFoodId(long favoriteFoodId) { this.favoriteFoodId = favoriteFoodId; }
    public String getFavoriteFoodName() { return favoriteFoodName; }
    public void setFavoriteFoodName(String favoriteFoodName) { this.favoriteFoodName = favoriteFoodName; }

    /** Nombre a mostrar en la celda del calendario, sea receta o favorito. */
    public String getDisplayName() {
        return recipeId > 0 ? recipeName : favoriteFoodName;
    }
}
