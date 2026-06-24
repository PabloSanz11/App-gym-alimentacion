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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Lista de TODAS las recetas precargadas, seccionadas por horario (mealSlot),
 *  usada en "Otras recetas" para poder agregar cualquier platillo sin importar
 *  la hora de comida que se esté registrando. */
public class AllRecipesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    private static final String[] SLOT_ORDER = {"desayuno", "almuerzo", "merienda", "cena"};

    private static String slotLabel(String slot) {
        if (slot == null) return "Otros";
        switch (slot) {
            case "desayuno": return "🌅 Desayuno (9am)";
            case "almuerzo": return "🍽 Almuerzo (12pm)";
            case "merienda": return "🥪 Merienda (4pm)";
            case "cena": return "🌙 Cena (8pm)";
            default: return slot;
        }
    }

    public interface OnRecipeClick { void onClick(Recipe recipe); }

    private final List<Object> rows = new ArrayList<>();
    private final OnRecipeClick listener;

    public AllRecipesAdapter(OnRecipeClick listener) {
        this.listener = listener;
    }

    public void setItems(List<Recipe> recipes) {
        rows.clear();
        Map<String, List<Recipe>> grouped = new LinkedHashMap<>();
        for (String slot : SLOT_ORDER) grouped.put(slot, new ArrayList<>());
        for (Recipe r : recipes) {
            grouped.computeIfAbsent(r.getMealSlot(), k -> new ArrayList<>()).add(r);
        }
        for (String slot : SLOT_ORDER) {
            List<Recipe> items = grouped.get(slot);
            if (items == null || items.isEmpty()) continue;
            rows.add(slotLabel(slot));
            rows.addAll(items);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shopping_category_header, parent, false);
            return new HeaderViewHolder(v);
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_recipe_grid, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText((String) rows.get(position));
            return;
        }
        Recipe r = (Recipe) rows.get(position);
        ItemViewHolder h = (ItemViewHolder) holder;
        String emoji = r.getImageEmoji() != null ? r.getImageEmoji() : "🍽";
        h.tvEmoji.setText(emoji);
        h.tvName.setText(r.getName());
        h.tvCategory.setText(r.getCategory() != null ? r.getCategory() : "");
        h.tvCals.setText(String.format("%.0f kcal", r.getTotalCaloriesKcal()));
        h.tvProtein.setText(String.format("P: %.0fg", r.getTotalProteinG()));
        h.itemView.setOnClickListener(v -> listener.onClick(r));
    }

    @Override
    public int getItemCount() { return rows.size(); }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(@NonNull View v) {
            super(v);
            tvHeader = (TextView) v;
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvCategory, tvCals, tvProtein;
        ItemViewHolder(@NonNull View v) {
            super(v);
            tvEmoji = v.findViewById(R.id.tv_recipe_emoji);
            tvName = v.findViewById(R.id.tv_recipe_name);
            tvCategory = v.findViewById(R.id.tv_recipe_category);
            tvCals = v.findViewById(R.id.tv_recipe_cals);
            tvProtein = v.findViewById(R.id.tv_recipe_protein_g);
        }
    }
}
