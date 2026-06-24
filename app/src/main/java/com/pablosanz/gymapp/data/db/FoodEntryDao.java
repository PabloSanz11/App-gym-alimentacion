package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.FoodEntry;

import java.util.List;

@Dao
public interface FoodEntryDao {

    @Insert
    long insert(FoodEntry entry);

    @Update
    void update(FoodEntry entry);

    @Delete
    void delete(FoodEntry entry);

    @Query("SELECT * FROM food_entries WHERE mealLogId = :mealLogId")
    List<FoodEntry> getByMealLogId(long mealLogId);

    @Query("SELECT * FROM food_entries WHERE id = :id")
    FoodEntry getById(long id);

    @Query("DELETE FROM food_entries WHERE mealLogId = :mealLogId")
    void deleteByMealLogId(long mealLogId);
}
