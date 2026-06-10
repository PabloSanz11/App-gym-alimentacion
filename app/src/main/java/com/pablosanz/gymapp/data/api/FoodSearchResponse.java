package com.pablosanz.gymapp.data.api;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class FoodSearchResponse {
    @SerializedName("products")
    private List<FoodProduct> products;

    public List<FoodProduct> getProducts() { return products; }
    public void setProducts(List<FoodProduct> products) { this.products = products; }
}
