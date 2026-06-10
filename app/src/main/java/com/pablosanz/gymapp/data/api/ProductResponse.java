package com.pablosanz.gymapp.data.api;

import com.google.gson.annotations.SerializedName;

public class ProductResponse {
    @SerializedName("product")
    private FoodProduct product;

    @SerializedName("status")
    private int status;

    public FoodProduct getProduct() { return product; }
    public void setProduct(FoodProduct product) { this.product = product; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }
}
