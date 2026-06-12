package com.pablosanz.gymapp.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.MealLog;

import java.util.List;

@Dao
public interface MealLogDao {

    @Insert
    long insert(MealLog mealLog);

    @Update
    void update(MealLog mealLog);

    @Delete
    void delete(MealLog mealLog);

    @Query("SELECT * FROM meal_logs WHERE date = :date")
    LiveData<List<MealLog>> getByDate(String date);

    @Query("SELECT * FROM meal_logs WHERE date = :date AND mealSlot = :mealSlot LIMIT 1")
    MealLog getByDateAndSlot(String date, String mealSlot);

    @Query("SELECT * FROM meal_logs WHERE date IN (:dates) ORDER BY date DESC")
    List<MealLog> getByDateRange(List<String> dates);

    @Query("SELECT * FROM meal_logs WHERE id = :id LIMIT 1")
    MealLog getById(long id);
}
