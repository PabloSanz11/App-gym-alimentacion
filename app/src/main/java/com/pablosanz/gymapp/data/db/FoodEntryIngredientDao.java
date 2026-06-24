package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.FoodEntryIngredient;

import java.util.List;

@Dao
public interface FoodEntryIngredientDao {

    @Insert
    void insert(FoodEntryIngredient ingredient);

    @Insert
    void insertAll(List<FoodEntryIngredient> ingredients);

    @Update
    void update(FoodEntryIngredient ingredient);

    @Delete
    void delete(FoodEntryIngredient ingredient);

    @Query("SELECT * FROM food_entry_ingredients WHERE foodEntryId = :foodEntryId")
    List<FoodEntryIngredient> getByFoodEntryId(long foodEntryId);
}
