package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.RecipeIngredient;

import java.util.List;

@Dao
public interface RecipeIngredientDao {
    @Insert
    void insert(RecipeIngredient ingredient);

    @Update
    void update(RecipeIngredient ingredient);

    @Delete
    void delete(RecipeIngredient ingredient);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredient> getByRecipe(long recipeId);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId")
    List<RecipeIngredient> getByRecipeId(long recipeId);
}
