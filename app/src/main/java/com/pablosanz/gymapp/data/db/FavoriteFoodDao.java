package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.FavoriteFood;

import java.util.List;

@Dao
public interface FavoriteFoodDao {
    @Insert
    long insert(FavoriteFood food);

    @Update
    void update(FavoriteFood food);

    @Delete
    void delete(FavoriteFood food);

    @Query("SELECT * FROM favorite_foods ORDER BY name ASC")
    List<FavoriteFood> getAll();

    @Query("SELECT * FROM favorite_foods WHERE mealSlot = :mealSlot OR mealSlot IS NULL ORDER BY name ASC")
    List<FavoriteFood> getByMealSlot(String mealSlot);

    @Query("SELECT * FROM favorite_foods WHERE id = :id LIMIT 1")
    FavoriteFood getById(long id);
}
