package com.pablosanz.gymapp.ui.nutrition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.FavoriteFood;

import java.util.List;

public class FavoriteFoodAdapter extends RecyclerView.Adapter<FavoriteFoodAdapter.ViewHolder> {

    public interface OnFavoriteClickListener {
        void onClick(FavoriteFood food);
    }

    private final List<FavoriteFood> items;
    private final OnFavoriteClickListener listener;

    public FavoriteFoodAdapter(List<FavoriteFood> items, OnFavoriteClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favorite_food, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavoriteFood food = items.get(position);
        holder.tvEmoji.setText(emojiFor(food.getName()));
        holder.tvName.setText(food.getName());
        holder.tvMacros.setText(String.format("%.0f kcal · P:%.0fg", food.getCaloriesKcal(), food.getProteinG()));
        holder.itemView.setOnClickListener(v -> listener.onClick(food));
    }

    @Override
    public int getItemCount() { return items.size(); }

    private String emojiFor(String name) {
        String n = name.toLowerCase();
        if (n.contains("pollo")) return "🍗";
        if (n.contains("carne") || n.contains("res")) return "🥩";
        if (n.contains("frijol") || n.contains("lenteja")) return "🫘";
        if (n.contains("huevo") || n.contains("clara")) return "🥚";
        if (n.contains("cottage") || n.contains("panela") || n.contains("queso")) return "🧀";
        if (n.contains("atún") || n.contains("atun")) return "🐟";
        if (n.contains("totopo") || n.contains("tortilla")) return "🌽";
        if (n.contains("salsa")) return "🌶️";
        if (n.contains("aguacate")) return "🥑";
        if (n.contains("kéfir") || n.contains("kefir") || n.contains("leche")) return "🥛";
        if (n.contains("proteína") || n.contains("proteina") || n.contains("whey")) return "🥤";
        if (n.contains("arroz")) return "🍚";
        if (n.contains("camote") || n.contains("papa")) return "🍠";
        if (n.contains("pasta")) return "🍝";
        if (n.contains("avena")) return "🥣";
        if (n.contains("plátano") || n.contains("platano") || n.contains("fruta")) return "🍌";
        if (n.contains("verdura") || n.contains("ensalada")) return "🥗";
        if (n.contains("nuez") || n.contains("nueces")) return "🥜";
        if (n.contains("crema de cacahuate")) return "🥜";
        if (n.contains("chorizo")) return "🌭";
        return "🍽️";
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmoji, tvName, tvMacros;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmoji = itemView.findViewById(R.id.tv_fav_emoji);
            tvName = itemView.findViewById(R.id.tv_fav_name);
            tvMacros = itemView.findViewById(R.id.tv_fav_macros);
        }
    }
}
