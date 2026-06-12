package com.pablosanz.gymapp.ui.gym;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.Exercise;
import com.pablosanz.gymapp.data.model.ExerciseData;

import java.util.List;

public class DayCardAdapter extends RecyclerView.Adapter<DayCardAdapter.ViewHolder> {

    public interface OnDayStartListener { void onStart(int dayType); }

    private static final String[] COLORS = {"#1B3A6B", "#16A34A", "#7C3AED", "#EA580C"};
    private static final String[] EMOJIS = {"💪", "🦵", "🏋️", "🔥"};

    private int suggestedDay = 1;
    private final OnDayStartListener listener;

    public DayCardAdapter(OnDayStartListener listener) {
        this.listener = listener;
    }

    public void setSuggestedDay(int day) {
        this.suggestedDay = day;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_day_card, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        int dayType = position + 1;
        List<Exercise> exercises = ExerciseData.getExercisesForDay(dayType);
        String color = COLORS[position];
        String emoji = EMOJIS[position];
        String dayName = ExerciseData.getDayName(dayType);

        h.layoutHeader.setBackgroundColor(Color.parseColor(color));
        h.tvEmoji.setText(emoji);
        h.tvDayNumber.setText("DÍA " + dayType);
        h.tvDayMuscle.setText(dayName);
        int totalSets = 0;
        for (Exercise e : exercises) totalSets += e.getSetsTarget();
        h.tvSetsCount.setText(exercises.size() + " ejercicios · " + totalSets + " series");

        // Suggested badge
        if (dayType == suggestedDay) {
            h.tvSuggestedBadge.setVisibility(View.VISIBLE);
        } else {
            h.tvSuggestedBadge.setVisibility(View.GONE);
        }

        // Exercise preview rows
        h.layoutExercises.removeAllViews();
        for (int i = 0; i < exercises.size(); i++) {
            Exercise ex = exercises.get(i);
            TextView tv = new TextView(h.itemView.getContext());
            tv.setText("  " + ex.getSetsTarget() + "×" + ex.getRepsTarget() + "   " + ex.getName());
            tv.setTextSize(13f);
            tv.setTextColor(Color.parseColor("#3C3C43"));
            int dp4 = (int)(2 * h.itemView.getContext().getResources().getDisplayMetrics().density);
            tv.setPadding(0, dp4, 0, dp4);
            h.layoutExercises.addView(tv);
        }

        h.btnStart.setBackgroundColor(Color.parseColor(color));
        h.btnStart.setText("Iniciar Día " + dayType);
        h.btnStart.setOnClickListener(v -> listener.onStart(dayType));
        h.itemView.setOnClickListener(v -> listener.onStart(dayType));
    }

    @Override
    public int getItemCount() { return 4; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutHeader, layoutExercises;
        TextView tvEmoji, tvDayNumber, tvDayMuscle, tvSetsCount, tvSuggestedBadge;
        com.google.android.material.button.MaterialButton btnStart;

        ViewHolder(@NonNull View v) {
            super(v);
            layoutHeader = v.findViewById(R.id.layout_day_header);
            layoutExercises = v.findViewById(R.id.layout_exercises);
            tvEmoji = v.findViewById(R.id.tv_day_emoji);
            tvDayNumber = v.findViewById(R.id.tv_day_number);
            tvDayMuscle = v.findViewById(R.id.tv_day_muscle);
            tvSetsCount = v.findViewById(R.id.tv_day_sets_count);
            tvSuggestedBadge = v.findViewById(R.id.tv_suggested_badge);
            btnStart = v.findViewById(R.id.btn_start_day);
        }
    }
}
