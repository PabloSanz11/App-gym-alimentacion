package com.pablosanz.gymapp.ui.shopping;

public class ShoppingItem {
    public final String name;
    public final String quantity;
    public final float priceEstimated;
    public final String category;
    public boolean checked;

    public ShoppingItem(String name, String quantity, float priceEstimated, String category) {
        this.name = name;
        this.quantity = quantity;
        this.priceEstimated = priceEstimated;
        this.category = category;
        this.checked = false;
    }
}
