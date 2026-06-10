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
        holder.tvName.setText(food.getName());
        holder.tvMacros.setText(String.format("%.0f kcal · P:%.0fg", food.getCaloriesKcal(), food.getProteinG()));
        holder.itemView.setOnClickListener(v -> listener.onClick(food));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvMacros;
        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_fav_name);
            tvMacros = itemView.findViewById(R.id.tv_fav_macros);
        }
    }
}
