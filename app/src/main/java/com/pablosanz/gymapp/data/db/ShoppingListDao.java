package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.ShoppingListItem;

import java.util.List;

@Dao
public interface ShoppingListDao {
    @Insert
    void insertAll(List<ShoppingListItem> items);

    @Update
    void update(ShoppingListItem item);

    @Query("SELECT * FROM shopping_list_items ORDER BY ingredientName ASC")
    List<ShoppingListItem> getAll();

    @Query("DELETE FROM shopping_list_items")
    void clearAll();
}
