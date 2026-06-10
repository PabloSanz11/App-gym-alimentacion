package com.pablosanz.gymapp.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "body_measurements")
public class BodyMeasurement {

    @PrimaryKey(autoGenerate = true)
    private long id;

    private String date; // "yyyy-MM-dd"
    private float weightKg;
    private float waistCm;
    private String notes;

    public BodyMeasurement() {}

    public BodyMeasurement(String date, float weightKg, float waistCm, String notes) {
        this.date = date;
        this.weightKg = weightKg;
        this.waistCm = waistCm;
        this.notes = notes;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public float getWeightKg() { return weightKg; }
    public void setWeightKg(float weightKg) { this.weightKg = weightKg; }

    public float getWaistCm() { return waistCm; }
    public void setWaistCm(float waistCm) { this.waistCm = waistCm; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
