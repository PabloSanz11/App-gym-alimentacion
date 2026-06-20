package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.pablosanz.gymapp.data.model.IngredientPriceInfo;

import java.util.List;

@Dao
public interface IngredientPriceInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void upsert(IngredientPriceInfo info);

    @Query("SELECT * FROM ingredient_price_info")
    List<IngredientPriceInfo> getAll();

    @Query("SELECT * FROM ingredient_price_info WHERE ingredientName = :ingredientName LIMIT 1")
    IngredientPriceInfo getByName(String ingredientName);
}
