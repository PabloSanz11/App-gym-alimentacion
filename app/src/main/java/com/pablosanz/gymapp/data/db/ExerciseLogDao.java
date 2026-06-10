package com.pablosanz.gymapp.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.pablosanz.gymapp.data.model.ExerciseLog;

import java.util.List;

@Dao
public interface ExerciseLogDao {

    @Insert
    long insert(ExerciseLog log);

    @Update
    void update(ExerciseLog log);

    @Delete
    void delete(ExerciseLog log);

    @Query("SELECT * FROM exercise_logs WHERE sessionId = :sessionId ORDER BY exerciseName, setNumber")
    List<ExerciseLog> getBySessionId(long sessionId);

    @Query("SELECT * FROM exercise_logs WHERE exerciseName = :exerciseName ORDER BY rowid DESC")
    List<ExerciseLog> getByExerciseName(String exerciseName);

    @Query("SELECT el.* FROM exercise_logs el INNER JOIN workout_sessions ws ON el.sessionId = ws.id WHERE el.exerciseName = :exerciseName ORDER BY ws.date DESC LIMIT 10")
    List<ExerciseLog> getRecentByExerciseName(String exerciseName);
}
