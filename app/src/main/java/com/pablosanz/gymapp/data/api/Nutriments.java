package com.pablosanz.gymapp.data.api;

import com.google.gson.annotations.SerializedName;

public class Nutriments {
    @SerializedName("energy-kcal_100g")
    private float energyKcal100g;

    @SerializedName("proteins_100g")
    private float proteins100g;

    @SerializedName("carbohydrates_100g")
    private float carbohydrates100g;

    @SerializedName("fat_100g")
    private float fat100g;

    public float getEnergy_100g() { return energyKcal100g; }
    public float getProteins_100g() { return proteins100g; }
    public float getCarbohydrates_100g() { return carbohydrates100g; }
    public float getFat_100g() { return fat100g; }
}
