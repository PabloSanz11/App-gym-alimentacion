package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pablosanz.gymapp.data.model.MealPlanEntry;

import java.util.List;

@Dao
public interface MealPlanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(MealPlanEntry entry);

    @Delete
    void delete(MealPlanEntry entry);

    @Query("SELECT * FROM meal_plan_entries")
    List<MealPlanEntry> getAll();

    @Query("SELECT * FROM meal_plan_entries WHERE dayOfWeek = :day AND mealSlot = :slot LIMIT 1")
    MealPlanEntry getByDayAndSlot(String day, String slot);

    @Query("DELETE FROM meal_plan_entries WHERE dayOfWeek = :day AND mealSlot = :slot")
    void clearSlot(String day, String slot);
}
