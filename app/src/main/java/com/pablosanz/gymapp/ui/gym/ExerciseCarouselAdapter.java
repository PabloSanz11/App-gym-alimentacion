package com.pablosanz.gymapp.ui.gym;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.textfield.TextInputEditText;
import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.Exercise;
import com.pablosanz.gymapp.data.model.ExerciseLog;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExerciseCarouselAdapter extends RecyclerView.Adapter<ExerciseCarouselAdapter.PageViewHolder> {

    public interface OnSetAdded {
        void onSetAdded(String exerciseName, float weightKg, int reps);
    }

    public static class SeriesEntry {
        public final float weightKg;
        public final int reps;
        public SeriesEntry(float weightKg, int reps) {
            this.weightKg = weightKg;
            this.reps = reps;
        }
    }

    private final List<Exercise> exercises;
    private final Map<String, List<ExerciseLog>> history;
    private final Map<String, List<SeriesEntry>> currentSets;
    private final OnSetAdded callback;

    public ExerciseCarouselAdapter(List<Exercise> exercises,
                                   Map<String, List<ExerciseLog>> history,
                                   Map<String, List<SeriesEntry>> currentSets,
                                   OnSetAdded callback) {
        this.exercises = exercises;
        this.history = history;
        this.currentSets = currentSets;
        this.callback = callback;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_page, parent, false);
        return new PageViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder h, int position) {
        Exercise ex = exercises.get(position);
        String name = ex.getName();

        h.tvName.setText(name);
        h.tvSetsTarget.setText(ex.getSetsTarget() + " series");
        h.tvRepsTarget.setText(ex.getRepsTarget() + " reps");

        // History
        List<ExerciseLog> logs = history.get(name);
        h.layoutHistoryRows.removeAllViews();
        if (logs == null || logs.isEmpty()) {
            h.tvNoHistory.setVisibility(View.VISIBLE);
        } else {
            h.tvNoHistory.setVisibility(View.GONE);
            // Group by session (first 3 distinct sessions)
            int shown = 0;
            String lastDate = null;
            StringBuilder sessionLine = new StringBuilder();
            int setNum = 0;
            for (ExerciseLog log : logs) {
                if (shown >= 3) break;
                // Just show last 5 individual sets
                TextView tv = new TextView(h.itemView.getContext());
                String dateStr = log.getSessionId() > 0 ? ("Ses. " + log.getSessionId()) : "";
                tv.setText("• Serie " + log.getSetNumber() + ": " + String.format("%.1f", log.getWeightKg()) + "kg × " + log.getReps() + " reps");
                tv.setTextSize(13f);
                tv.setTextColor(Color.parseColor("#64748B"));
                int dp2 = (int)(2 * h.itemView.getContext().getResources().getDisplayMetrics().density);
                tv.setPadding(0, dp2, 0, dp2);
                h.layoutHistoryRows.addView(tv);
                shown++;
            }
        }

        // Pre-fill weight from history
        List<ExerciseLog> histLogs = history.get(name);
        if (histLogs != null && !histLogs.isEmpty()) {
            float lastWeight = histLogs.get(0).getWeightKg();
            if (lastWeight > 0) h.etWeight.setText(String.format("%.1f", lastWeight));
        }

        // Current session sets
        refreshSets(h, name);

        // Add set button
        h.btnAddSet.setOnClickListener(v -> {
            String wStr = h.etWeight.getText() != null ? h.etWeight.getText().toString().trim() : "";
            String rStr = h.etReps.getText() != null ? h.etReps.getText().toString().trim() : "";
            if (wStr.isEmpty() || rStr.isEmpty()) return;
            try {
                float w = Float.parseFloat(wStr);
                int r = Integer.parseInt(rStr);
                if (!currentSets.containsKey(name)) currentSets.put(name, new ArrayList<>());
                currentSets.get(name).add(new SeriesEntry(w, r));
                refreshSets(h, name);
                callback.onSetAdded(name, w, r);
                h.tvNoSets.setVisibility(View.GONE);
                // Auto-increment weight slightly for encouragement (optional: leave as is)
            } catch (NumberFormatException ignored) {}
        });
    }

    private void refreshSets(PageViewHolder h, String name) {
        h.layoutCurrentSets.removeAllViews();
        List<SeriesEntry> sets = currentSets.get(name);
        if (sets == null || sets.isEmpty()) {
            h.tvNoSets.setVisibility(View.VISIBLE);
            return;
        }
        h.tvNoSets.setVisibility(View.GONE);
        for (int i = 0; i < sets.size(); i++) {
            SeriesEntry s = sets.get(i);
            TextView tv = new TextView(h.itemView.getContext());
            tv.setText("✓ Serie " + (i + 1) + ":  " + String.format("%.1f", s.weightKg) + " kg × " + s.reps + " reps");
            tv.setTextSize(14f);
            tv.setTextColor(Color.parseColor("#1B3A6B"));
            tv.setTypeface(null, android.graphics.Typeface.BOLD);
            int dp4 = (int)(4 * h.itemView.getContext().getResources().getDisplayMetrics().density);
            tv.setPadding(0, dp4, 0, dp4);
            h.layoutCurrentSets.addView(tv);
        }
    }

    @Override
    public int getItemCount() { return exercises.size(); }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvSetsTarget, tvRepsTarget, tvNoHistory, tvNoSets;
        LinearLayout layoutHistoryRows, layoutCurrentSets;
        TextInputEditText etWeight, etReps;
        com.google.android.material.button.MaterialButton btnAddSet;

        PageViewHolder(@NonNull View v) {
            super(v);
            tvName = v.findViewById(R.id.tv_exercise_name);
            tvSetsTarget = v.findViewById(R.id.tv_sets_target);
            tvRepsTarget = v.findViewById(R.id.tv_reps_target);
            tvNoHistory = v.findViewById(R.id.tv_no_history);
            tvNoSets = v.findViewById(R.id.tv_no_sets);
            layoutHistoryRows = v.findViewById(R.id.layout_history_rows);
            layoutCurrentSets = v.findViewById(R.id.layout_current_sets);
            etWeight = v.findViewById(R.id.et_weight);
            etReps = v.findViewById(R.id.et_reps);
            btnAddSet = v.findViewById(R.id.btn_add_set);
        }
    }
}
