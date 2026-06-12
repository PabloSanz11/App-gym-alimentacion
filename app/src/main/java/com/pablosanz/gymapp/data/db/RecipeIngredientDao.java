package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.pablosanz.gymapp.data.model.RecipeIngredient;

import java.util.List;

@Dao
public interface RecipeIngredientDao {
    @Insert
    void insert(RecipeIngredient ingredient);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredient> getByRecipe(long recipeId);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredient> getByRecipeId(long recipeId);
}
