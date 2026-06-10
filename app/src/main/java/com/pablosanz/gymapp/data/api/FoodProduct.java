package com.pablosanz.gymapp.data.api;

import com.google.gson.annotations.SerializedName;

public class FoodProduct {
    @SerializedName("product_name")
    private String product_name;

    @SerializedName("code")
    private String code;

    @SerializedName("nutriments")
    private Nutriments nutriments;

    public String getProduct_name() { return product_name; }
    public void setProduct_name(String product_name) { this.product_name = product_name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public Nutriments getNutriments() { return nutriments; }
    public void setNutriments(Nutriments nutriments) { this.nutriments = nutriments; }
}
