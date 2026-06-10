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

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.ViewHolder> {

    public interface OnRecipeClickListener { void onClick(Recipe recipe); }

    private List<Recipe> items = new ArrayList<>();
    private final OnRecipeClickListener listener;

    public RecipeAdapter(OnRecipeClickListener listener) { this.listener = listener; }

    public void setItems(List<Recipe> items) {
        this.items = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe r = items.get(position);
        holder.tvEmoji.setText(r.getImageEmoji());
        holder.tvName.setText(r.getName());
        holder.tvDescription.setText(r.getDescription());
        holder.tvCategory.setText(r.getCategory());
        holder.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvDescription, tvCategory;
        ViewHolder(@NonNull View v) {
            super(v);
            tvEmoji = v.findViewById(R.id.tv_recipe_emoji);
            tvName = v.findViewById(R.id.tv_recipe_name);
            tvDescription = v.findViewById(R.id.tv_recipe_description);
            tvCategory = v.findViewById(R.id.tv_recipe_category);
        }
    }
}
