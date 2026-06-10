package com.pablosanz.gymapp.ui.nutrition;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.data.api.FoodProduct;
import com.pablosanz.gymapp.databinding.ItemFoodResultBinding;

import java.util.List;

public class FoodSearchAdapter extends RecyclerView.Adapter<FoodSearchAdapter.ViewHolder> {

    public interface OnFoodSelectedListener {
        void onFoodSelected(FoodProduct product);
    }

    private List<FoodProduct> products;
    private final OnFoodSelectedListener listener;

    public FoodSearchAdapter(List<FoodProduct> products, OnFoodSelectedListener listener) {
        this.products = products;
        this.listener = listener;
    }

    public void updateData(List<FoodProduct> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodResultBinding binding = ItemFoodResultBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FoodProduct product = products.get(position);
        holder.bind(product, listener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemFoodResultBinding binding;

        ViewHolder(ItemFoodResultBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(FoodProduct product, OnFoodSelectedListener listener) {
            String name = product.getProduct_name();
            binding.tvFoodName.setText(name != null && !name.isEmpty() ? name : "Producto sin nombre");

            if (product.getNutriments() != null) {
                binding.tvFoodMacros.setText(String.format(
                        "Por 100g: %.0f kcal | P:%.1fg C:%.1fg G:%.1fg",
                        product.getNutriments().getEnergy_100g(),
                        product.getNutriments().getProteins_100g(),
                        product.getNutriments().getCarbohydrates_100g(),
                        product.getNutriments().getFat_100g()));
            } else {
                binding.tvFoodMacros.setText("Sin información nutricional");
            }

            binding.btnSelectFood.setOnClickListener(v -> listener.onFoodSelected(product));
        }
    }
}
