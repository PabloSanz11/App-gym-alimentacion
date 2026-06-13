package com.pablosanz.gymapp.ui.gym;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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

    private static final int[] HEADER_IMAGES = {
        R.drawable.day1_header,
        R.drawable.day2_header,
        R.drawable.day3_header,
        R.drawable.day4_header,
    };

    private static final int[] ACCENT_COLORS = {
        0xFF1B3A6B,
        0xFF16A34A,
        0xFF7C3AED,
        0xFFEA580C,
    };

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
        int accentColor = ACCENT_COLORS[position];
        String dayName = ExerciseData.getDayName(dayType);

        // Header image
        h.ivHeader.setImageResource(HEADER_IMAGES[position]);

        h.tvDayNumber.setText("DÍA " + dayType);
        h.tvDayMuscle.setText(dayName);

        int totalSets = 0;
        for (Exercise e : exercises) totalSets += e.getSetsTarget();
        h.tvSetsCount.setText(exercises.size() + " ejercicios  ·  " + totalSets + " series totales");

        h.tvSuggestedBadge.setVisibility(dayType == suggestedDay ? View.VISIBLE : View.GONE);

        // Full exercise list (no limit)
        h.layoutExercises.removeAllViews();
        for (Exercise ex : exercises) {
            TextView tv = new TextView(h.itemView.getContext());
            tv.setText(ex.getSetsTarget() + "×" + ex.getRepsTarget() + "  " + ex.getName());
            tv.setTextSize(13f);
            tv.setTextColor(Color.parseColor("#3C3C43"));
            float dp = h.itemView.getContext().getResources().getDisplayMetrics().density;
            tv.setPadding(0, (int)(3 * dp), 0, (int)(3 * dp));
            h.layoutExercises.addView(tv);
        }

        // Button color matches day accent
        h.btnStart.setBackgroundTintList(ColorStateList.valueOf(accentColor));
        h.btnStart.setText("Iniciar Día " + dayType);
        h.btnStart.setOnClickListener(v -> listener.onStart(dayType));
    }

    @Override
    public int getItemCount() { return 4; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivHeader;
        LinearLayout layoutExercises;
        TextView tvDayNumber, tvDayMuscle, tvSetsCount, tvSuggestedBadge;
        com.google.android.material.button.MaterialButton btnStart;

        ViewHolder(@NonNull View v) {
            super(v);
            ivHeader = v.findViewById(R.id.iv_day_header);
            layoutExercises = v.findViewById(R.id.layout_exercises);
            tvDayNumber = v.findViewById(R.id.tv_day_number);
            tvDayMuscle = v.findViewById(R.id.tv_day_muscle);
            tvSetsCount = v.findViewById(R.id.tv_day_sets_count);
            tvSuggestedBadge = v.findViewById(R.id.tv_suggested_badge);
            btnStart = v.findViewById(R.id.btn_start_day);
        }
    }
}
