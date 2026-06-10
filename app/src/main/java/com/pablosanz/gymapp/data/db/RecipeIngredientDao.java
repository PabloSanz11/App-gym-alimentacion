package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import java.util.List;

@Dao
public interface RecipeIngredientDao {
    @Insert
    long insert(RecipeIngredient ingredient);

    @Update
    void update(RecipeIngredient ingredient);

    @Query("SELECT * FROM recipe_ingredients WHERE recipeId = :recipeId ORDER BY id ASC")
    List<RecipeIngredient> getByRecipeId(long recipeId);
}
