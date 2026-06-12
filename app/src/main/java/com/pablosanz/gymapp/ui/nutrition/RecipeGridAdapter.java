package com.pablosanz.gymapp.ui.nutrition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeGridAdapter extends RecyclerView.Adapter<RecipeGridAdapter.ViewHolder> {

    public interface OnRecipeClick { void onClick(Recipe recipe); }

    private List<Recipe> items = new ArrayList<>();
    private final OnRecipeClick listener;

    public RecipeGridAdapter(OnRecipeClick listener) {
        this.listener = listener;
    }

    public void setItems(List<Recipe> recipes) {
        this.items = new ArrayList<>(recipes);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_grid, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe r = items.get(position);
        String emoji = r.getImageEmoji() != null ? r.getImageEmoji() : "🍽";
        holder.tvEmoji.setText(emoji);
        holder.tvName.setText(r.getName());
        holder.tvCategory.setText(r.getCategory() != null ? r.getCategory() : "");
        holder.tvCals.setText(String.format("%.0f kcal", r.getTotalCaloriesKcal()));
        holder.tvProtein.setText(String.format("P: %.0fg", r.getTotalProteinG()));
        holder.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvCategory, tvCals, tvProtein;
        ViewHolder(@NonNull View v) {
            super(v);
            tvEmoji = v.findViewById(R.id.tv_recipe_emoji);
            tvName = v.findViewById(R.id.tv_recipe_name);
            tvCategory = v.findViewById(R.id.tv_recipe_category);
            tvCals = v.findViewById(R.id.tv_recipe_cals);
            tvProtein = v.findViewById(R.id.tv_recipe_protein_g);
        }
    }
}
