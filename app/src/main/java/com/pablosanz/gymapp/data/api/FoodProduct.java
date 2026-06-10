package com.pablosanz.gymapp.data.api;

import com.google.gson.annotations.SerializedName;

public class FoodProduct {
    @SerializedName("product_name")
    private String product_name;

    @SerializedName("code")
    private String code;

    @SerializedName("nutriments")
    private Nutriments nutriments;

    @SerializedName("brands")
    private String brands;

    public String getProduct_name() { return product_name; }
    public String getCode() { return code; }
    public Nutriments getNutriments() { return nutriments; }
    public String getBrands() { return brands; }
}
