package com.pablosanz.gymapp.ui.nutrition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.RecipeIngredient;

import java.util.List;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder> {

    private final List<RecipeIngredient> items;

    public RecipeIngredientAdapter(List<RecipeIngredient> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_ingredient, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        RecipeIngredient ing = items.get(position);
        holder.tvName.setText(ing.getIngredientName());
        holder.tvQty.setText(String.format("%.0fg", ing.getQuantityG()));
        holder.tvMacros.setText(String.format("P:%.0f C:%.0f %.0fkcal",
                ing.getProteinG(), ing.getCarbsG(), ing.getCaloriesKcal()));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvQty, tvMacros;
        ViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_ingredient_name);
            tvQty = v.findViewById(R.id.tv_ingredient_qty);
            tvMacros = v.findViewById(R.id.tv_ingredient_macros);
        }
    }
}
