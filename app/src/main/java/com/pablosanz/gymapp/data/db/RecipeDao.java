package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import com.pablosanz.gymapp.data.model.Recipe;
import java.util.List;

@Dao
public interface RecipeDao {
    @Insert
    long insert(Recipe recipe);

    @Query("SELECT * FROM recipes ORDER BY name ASC")
    List<Recipe> getAll();

    @Query("SELECT * FROM recipes WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    List<Recipe> search(String query);

    @Query("SELECT * FROM recipes WHERE category = :category OR category = 'cualquiera' ORDER BY name ASC")
    List<Recipe> getByCategory(String category);
}
