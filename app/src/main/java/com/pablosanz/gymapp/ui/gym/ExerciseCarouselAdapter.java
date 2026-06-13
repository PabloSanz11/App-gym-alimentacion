package com.pablosanz.gymapp.ui.gym;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
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

    public interface OnSetChanged {
        void onSetAdded(String exerciseName, float weightKg, int reps);
        void onSetRemoved(String exerciseName, float weightKg, int reps);
        void onSetEdited(String exerciseName, float oldWeightKg, int oldReps, float newWeightKg, int newReps);
    }

    public static class SeriesEntry {
        public float weightKg;
        public int reps;
        public SeriesEntry(float weightKg, int reps) {
            this.weightKg = weightKg;
            this.reps = reps;
        }
    }

    private final List<Exercise> exercises;
    private final Map<String, List<ExerciseLog>> history;
    private final Map<String, List<SeriesEntry>> currentSets;
    private final OnSetChanged callback;

    public ExerciseCarouselAdapter(List<Exercise> exercises,
                                   Map<String, List<ExerciseLog>> history,
                                   Map<String, List<SeriesEntry>> currentSets,
                                   OnSetChanged callback) {
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

        // History section
        List<ExerciseLog> logs = history.get(name);
        h.layoutHistoryRows.removeAllViews();
        if (logs == null || logs.isEmpty()) {
            h.tvNoHistory.setVisibility(View.VISIBLE);
        } else {
            h.tvNoHistory.setVisibility(View.GONE);
            int shown = 0;
            for (ExerciseLog log : logs) {
                if (shown >= 3) break;
                TextView tv = new TextView(h.itemView.getContext());
                tv.setText("• Serie " + log.getSetNumber() + ": "
                        + String.format("%.1f", log.getWeightKg()) + "kg × " + log.getReps() + " reps");
                tv.setTextSize(13f);
                tv.setTextColor(Color.parseColor("#64748B"));
                int dp2 = (int)(2 * h.itemView.getContext().getResources().getDisplayMetrics().density);
                tv.setPadding(0, dp2, 0, dp2);
                h.layoutHistoryRows.addView(tv);
                shown++;
            }
        }

        // Pre-fill weight from history
        if (logs != null && !logs.isEmpty() && logs.get(0).getWeightKg() > 0) {
            h.etWeight.setText(String.format("%.1f", logs.get(0).getWeightKg()));
        }

        refreshSets(h, name);

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
        float dp = h.itemView.getContext().getResources().getDisplayMetrics().density;
        for (int i = 0; i < sets.size(); i++) {
            final int idx = i;
            SeriesEntry s = sets.get(i);

            LinearLayout row = new LinearLayout(h.itemView.getContext());
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            int dp4 = (int)(4 * dp);
            int dp6 = (int)(6 * dp);
            row.setPadding(0, dp4, 0, dp4);

            TextView tvSet = new TextView(h.itemView.getContext());
            tvSet.setText("✓ Serie " + (idx + 1) + ":  "
                    + String.format("%.1f", s.weightKg) + " kg × " + s.reps + " reps");
            tvSet.setTextSize(14f);
            tvSet.setTextColor(Color.parseColor("#1B3A6B"));
            tvSet.setTypeface(null, Typeface.BOLD);
            LinearLayout.LayoutParams tvParams = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f);
            tvSet.setLayoutParams(tvParams);

            TextView tvEdit = new TextView(h.itemView.getContext());
            tvEdit.setText("✎");
            tvEdit.setTextSize(16f);
            tvEdit.setTextColor(Color.parseColor("#9CA3AF"));
            tvEdit.setPadding(dp6, dp4, dp4, dp4);

            row.addView(tvSet);
            row.addView(tvEdit);

            row.setOnClickListener(v -> showEditDialog(h, name, idx, sets.get(idx)));
            row.setOnLongClickListener(v -> {
                showDeleteConfirm(h, name, idx, sets.get(idx));
                return true;
            });
            tvEdit.setOnClickListener(v -> showEditDialog(h, name, idx, sets.get(idx)));

            h.layoutCurrentSets.addView(row);
        }
    }

    private void showEditDialog(PageViewHolder h, String name, int idx, SeriesEntry entry) {
        LinearLayout layout = new LinearLayout(h.itemView.getContext());
        layout.setOrientation(LinearLayout.VERTICAL);
        int dp16 = (int)(16 * h.itemView.getContext().getResources().getDisplayMetrics().density);
        layout.setPadding(dp16, dp16, dp16, 0);

        EditText etW = new EditText(h.itemView.getContext());
        etW.setHint("Peso (kg)");
        etW.setText(String.format("%.1f", entry.weightKg));
        etW.setInputType(android.text.InputType.TYPE_CLASS_NUMBER | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(etW);

        EditText etR = new EditText(h.itemView.getContext());
        etR.setHint("Reps");
        etR.setText(String.valueOf(entry.reps));
        etR.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        layout.addView(etR);

        new AlertDialog.Builder(h.itemView.getContext())
                .setTitle("Editar serie " + (idx + 1))
                .setView(layout)
                .setPositiveButton("Guardar", (d, w) -> {
                    try {
                        float newW = Float.parseFloat(etW.getText().toString().trim());
                        int newR = Integer.parseInt(etR.getText().toString().trim());
                        float oldW = entry.weightKg;
                        int oldR = entry.reps;
                        entry.weightKg = newW;
                        entry.reps = newR;
                        refreshSets(h, name);
                        callback.onSetEdited(name, oldW, oldR, newW, newR);
                    } catch (NumberFormatException ignored) {}
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void showDeleteConfirm(PageViewHolder h, String name, int idx, SeriesEntry entry) {
        new AlertDialog.Builder(h.itemView.getContext())
                .setTitle("Eliminar serie " + (idx + 1))
                .setMessage(String.format("%.1f", entry.weightKg) + " kg × " + entry.reps + " reps")
                .setPositiveButton("Eliminar", (d, w) -> {
                    List<SeriesEntry> sets = currentSets.get(name);
                    if (sets != null && idx < sets.size()) {
                        SeriesEntry removed = sets.remove(idx);
                        refreshSets(h, name);
                        callback.onSetRemoved(name, removed.weightKg, removed.reps);
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
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
