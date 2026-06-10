package com.pablosanz.gymapp.ui.nutrition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.RecipeIngredient;

import java.util.ArrayList;
import java.util.List;

public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.ViewHolder> {

    private List<RecipeIngredient> items = new ArrayList<>();
    private OnTotalsChangedListener listener;

    public interface OnTotalsChangedListener {
        void onChanged();
    }

    public void setOnTotalsChangedListener(OnTotalsChangedListener l) { this.listener = l; }

    public void setItems(List<RecipeIngredient> items) {
        this.items = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    public List<RecipeIngredient> getItems() { return items; }

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
        holder.cbInclude.setChecked(ing.isIncluded());
        holder.tvName.setText(ing.getName());
        holder.tvQuantity.setText(String.format("%.0fg · %.0f kcal · P:%.0fg",
                ing.getQuantityG(), ing.getCaloriesKcal(), ing.getProteinG()));

        if (ing.getAlternativeNote() != null && !ing.getAlternativeNote().isEmpty()) {
            holder.tvNote.setText(ing.getAlternativeNote());
            holder.tvNote.setVisibility(View.VISIBLE);
        } else {
            holder.tvNote.setVisibility(View.GONE);
        }

        holder.cbInclude.setOnCheckedChangeListener((btn, checked) -> {
            items.get(holder.getAdapterPosition()).setIncluded(checked);
            if (listener != null) listener.onChanged();
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbInclude;
        TextView tvName, tvQuantity, tvNote;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cbInclude = itemView.findViewById(R.id.cb_include);
            tvName = itemView.findViewById(R.id.tv_ingredient_name);
            tvQuantity = itemView.findViewById(R.id.tv_ingredient_quantity);
            tvNote = itemView.findViewById(R.id.tv_ingredient_note);
        }
    }
}
