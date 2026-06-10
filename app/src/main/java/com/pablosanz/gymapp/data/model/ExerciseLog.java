package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "exercise_logs",
        foreignKeys = @ForeignKey(
                entity = WorkoutSession.class,
                parentColumns = "id",
                childColumns = "sessionId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("sessionId")})
public class ExerciseLog {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private long sessionId;
    private String exerciseName;
    private int setNumber;
    private int reps;
    private float weightKg;
    private String notes;

    public ExerciseLog() {}

    public ExerciseLog(long sessionId, String exerciseName, int setNumber, int reps, float weightKg, String notes) {
        this.sessionId = sessionId;
        this.exerciseName = exerciseName;
        this.setNumber = setNumber;
        this.reps = reps;
        this.weightKg = weightKg;
        this.notes = notes;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public long getSessionId() { return sessionId; }
    public void setSessionId(long sessionId) { this.sessionId = sessionId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public int getSetNumber() { return setNumber; }
    public void setSetNumber(int setNumber) { this.setNumber = setNumber; }

    public int getReps() { return reps; }
    public void setReps(int reps) { this.reps = reps; }

    public float getWeightKg() { return weightKg; }
    public void setWeightKg(float weightKg) { this.weightKg = weightKg; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
