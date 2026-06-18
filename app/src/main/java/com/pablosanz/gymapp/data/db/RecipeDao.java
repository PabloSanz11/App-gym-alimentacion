package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.Recipe;

import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    long insert(Recipe recipe);

    @Update
    void update(Recipe recipe);

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<Recipe> getAllRecipes();

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<Recipe> getAll();

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' ORDER BY name ASC")
    List<Recipe> search(String query);

    @Query("SELECT * FROM recipes WHERE category = :category ORDER BY name ASC")
    List<Recipe> getByCategory(String category);

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    Recipe getById(long id);
}
