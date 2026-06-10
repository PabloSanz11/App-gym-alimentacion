package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "workout_sessions")
public class WorkoutSession {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String date; // "yyyy-MM-dd"
    private int dayType; // 1-4
    private int durationMin;
    private String notes;

    public WorkoutSession() {}

    public WorkoutSession(String date, int dayType, int durationMin, String notes) {
        this.date = date;
        this.dayType = dayType;
        this.durationMin = durationMin;
        this.notes = notes;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public int getDayType() { return dayType; }
    public void setDayType(int dayType) { this.dayType = dayType; }

    public int getDurationMin() { return durationMin; }
    public void setDurationMin(int durationMin) { this.durationMin = durationMin; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
