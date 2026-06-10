package com.pablosanz.gymapp.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.WorkoutSession;

import java.util.List;

@Dao
public interface WorkoutSessionDao {

    @Insert
    long insert(WorkoutSession session);

    @Update
    void update(WorkoutSession session);

    @Delete
    void delete(WorkoutSession session);

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC")
    LiveData<List<WorkoutSession>> getAll();

    @Query("SELECT * FROM workout_sessions WHERE date = :date ORDER BY id DESC")
    List<WorkoutSession> getByDate(String date);

    @Query("SELECT * FROM workout_sessions WHERE date BETWEEN :startDate AND :endDate ORDER BY date DESC")
    List<WorkoutSession> getByDateRange(String startDate, String endDate);

    @Query("SELECT * FROM workout_sessions ORDER BY date DESC, id DESC LIMIT 1")
    WorkoutSession getLastSession();
}
