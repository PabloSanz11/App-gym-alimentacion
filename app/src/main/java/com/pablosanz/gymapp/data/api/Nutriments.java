package com.pablosanz.gymapp.data.api;

import com.google.gson.annotations.SerializedName;

public class Nutriments {
    @SerializedName("energy-kcal_100g")
    private float energy_100g;

    @SerializedName("proteins_100g")
    private float proteins_100g;

    @SerializedName("carbohydrates_100g")
    private float carbohydrates_100g;

    @SerializedName("fat_100g")
    private float fat_100g;

    public float getEnergy_100g() { return energy_100g; }
    public void setEnergy_100g(float energy_100g) { this.energy_100g = energy_100g; }

    public float getProteins_100g() { return proteins_100g; }
    public void setProteins_100g(float proteins_100g) { this.proteins_100g = proteins_100g; }

    public float getCarbohydrates_100g() { return carbohydrates_100g; }
    public void setCarbohydrates_100g(float carbohydrates_100g) { this.carbohydrates_100g = carbohydrates_100g; }

    public float getFat_100g() { return fat_100g; }
    public void setFat_100g(float fat_100g) { this.fat_100g = fat_100g; }
}
