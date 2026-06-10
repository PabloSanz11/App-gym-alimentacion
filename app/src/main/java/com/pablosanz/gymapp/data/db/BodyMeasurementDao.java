package com.pablosanz.gymapp.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.BodyMeasurement;

import java.util.List;

@Dao
public interface BodyMeasurementDao {

    @Insert
    long insert(BodyMeasurement measurement);

    @Update
    void update(BodyMeasurement measurement);

    @Delete
    void delete(BodyMeasurement measurement);

    @Query("SELECT * FROM body_measurements ORDER BY date DESC")
    LiveData<List<BodyMeasurement>> getAll();

    @Query("SELECT * FROM body_measurements ORDER BY date DESC LIMIT :limit")
    List<BodyMeasurement> getRecent(int limit);
}
