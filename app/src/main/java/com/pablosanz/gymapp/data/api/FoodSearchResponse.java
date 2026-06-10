package com.pablosanz.gymapp.data.api;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class FoodSearchResponse {
    @SerializedName("products")
    private List<FoodProduct> products;

    @SerializedName("count")
    private int count;

    public List<FoodProduct> getProducts() { return products; }
    public int getCount() { return count; }
}
