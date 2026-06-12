package com.pablosanz.gymapp.ui.nutrition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class WeeklyHistoryAdapter extends RecyclerView.Adapter<WeeklyHistoryAdapter.ViewHolder> {

    public static class DaySummary {
        public final String date;
        public final float calories;
        public final float protein;

        public DaySummary(String date, float calories, float protein) {
            this.date = date;
            this.calories = calories;
            this.protein = protein;
        }
    }

    private List<DaySummary> items;

    public WeeklyHistoryAdapter(List<DaySummary> items) {
        this.items = items;
    }

    public void setItems(List<DaySummary> items) {
        this.items = new ArrayList<>(items);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_weekly_day, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        DaySummary d = items.get(position);
        h.tvDate.setText(DateUtils.formatShort(d.date));
        h.tvCalories.setText(String.format("%.0f kcal", d.calories));
        h.tvProtein.setText(String.format("P: %.0fg", d.protein));
        h.progressProtein.setMax(170);
        h.progressProtein.setProgress((int) Math.min(d.protein, 170));
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvCalories, tvProtein;
        ProgressBar progressProtein;

        ViewHolder(@NonNull View v) {
            super(v);
            tvDate = v.findViewById(R.id.tv_day_date);
            tvCalories = v.findViewById(R.id.tv_day_calories);
            tvProtein = v.findViewById(R.id.tv_day_protein);
            progressProtein = v.findViewById(R.id.progress_day_protein);
        }
    }
}
