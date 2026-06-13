package com.pablosanz.gymapp.ui.gym;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

    // Gradient pairs: [top color, bottom color]
    private static final int[][] GRADIENTS = {
        {0xFF1B3A6B, 0xFF2D5FA6},  // Día 1 — azul navy
        {0xFF16A34A, 0xFF22C55E},  // Día 2 — verde
        {0xFF7C3AED, 0xFF9F5CF6},  // Día 3 — morado
        {0xFFEA580C, 0xFFF97316},  // Día 4 — naranja
    };
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
        int[] gradient = GRADIENTS[position];
        String emoji = EMOJIS[position];
        String dayName = ExerciseData.getDayName(dayType);

        // Gradient background on hero section
        GradientDrawable gd = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM,
                new int[]{gradient[0], gradient[1]});
        h.frameHeader.setBackground(gd);

        h.tvEmoji.setText(emoji);
        h.tvDayNumber.setText("DÍA " + dayType);
        h.tvDayMuscle.setText(dayName);

        int totalSets = 0;
        for (Exercise e : exercises) totalSets += e.getSetsTarget();
        h.tvSetsCount.setText(exercises.size() + " ejercicios  ·  " + totalSets + " series totales");

        h.tvSuggestedBadge.setVisibility(dayType == suggestedDay ? View.VISIBLE : View.GONE);

        // Exercise preview rows (show first 5)
        h.layoutExercises.removeAllViews();
        int shown = Math.min(exercises.size(), 5);
        for (int i = 0; i < shown; i++) {
            Exercise ex = exercises.get(i);
            TextView tv = new TextView(h.itemView.getContext());
            tv.setText(ex.getSetsTarget() + "×" + ex.getRepsTarget() + "  " + ex.getName());
            tv.setTextSize(13f);
            tv.setTextColor(Color.parseColor("#3C3C43"));
            float dp = h.itemView.getContext().getResources().getDisplayMetrics().density;
            tv.setPadding(0, (int)(2 * dp), 0, (int)(2 * dp));
            h.layoutExercises.addView(tv);
        }
        if (exercises.size() > 5) {
            TextView more = new TextView(h.itemView.getContext());
            more.setText("+" + (exercises.size() - 5) + " más...");
            more.setTextSize(12f);
            more.setTextColor(Color.parseColor("#9CA3AF"));
            h.layoutExercises.addView(more);
        }

        // Iniciar button color matches gradient
        h.btnStart.setBackgroundTintList(ColorStateList.valueOf(gradient[0]));
        h.btnStart.setText("Iniciar Día " + dayType);
        h.btnStart.setOnClickListener(v -> listener.onStart(dayType));
    }

    @Override
    public int getItemCount() { return 4; }

    static class ViewHolder extends RecyclerView.ViewHolder {
        FrameLayout frameHeader;
        LinearLayout layoutExercises;
        TextView tvEmoji, tvDayNumber, tvDayMuscle, tvSetsCount, tvSuggestedBadge;
        com.google.android.material.button.MaterialButton btnStart;

        ViewHolder(@NonNull View v) {
            super(v);
            frameHeader = v.findViewById(R.id.layout_day_header);
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
