package com.pablosanz.gymapp.ui.gym;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.data.model.Exercise;
import com.pablosanz.gymapp.databinding.ItemExerciseSetBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExerciseSetAdapter extends RecyclerView.Adapter<ExerciseSetAdapter.ViewHolder> {

    public static class ExerciseSetData {
        public Exercise exercise;
        public int setNumber;
        public int reps;
        public float weightKg;
        public String notes;

        public ExerciseSetData(Exercise exercise, int setNumber) {
            this.exercise = exercise;
            this.setNumber = setNumber;
            this.reps = 0;
            this.weightKg = 0;
            this.notes = "";
        }
    }

    private final List<ExerciseSetData> items;
    private final Map<String, Float> lastWeights;

    public ExerciseSetAdapter(List<Exercise> exercises, Map<String, Float> lastWeights) {
        this.lastWeights = lastWeights;
        this.items = new ArrayList<>();
        for (Exercise exercise : exercises) {
            for (int i = 1; i <= exercise.getSetsTarget(); i++) {
                items.add(new ExerciseSetData(exercise, i));
            }
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExerciseSetBinding binding = ItemExerciseSetBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ExerciseSetData data = items.get(position);
        holder.bind(data, lastWeights);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<ExerciseSetData> getItems() {
        return items;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemExerciseSetBinding binding;

        ViewHolder(ItemExerciseSetBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ExerciseSetData data, Map<String, Float> lastWeights) {
            binding.tvExerciseName.setText(data.exercise.getName());
            binding.tvSetNumber.setText("Serie " + data.setNumber + "/" + data.exercise.getSetsTarget());
            binding.tvTargetReps.setText("Meta: " + data.exercise.getRepsTarget() + " reps");

            binding.etReps.setText(data.reps > 0 ? String.valueOf(data.reps) : "");
            binding.etWeight.setText(data.weightKg > 0 ? String.valueOf(data.weightKg) : "");

            // Show previous weight as hint
            if (lastWeights != null && lastWeights.containsKey(data.exercise.getName())) {
                float prev = lastWeights.get(data.exercise.getName());
                binding.etWeight.setHint(String.format("Anterior: %.1f kg", prev));
            } else {
                binding.etWeight.setHint("kg");
            }

            binding.etReps.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        data.reps = Integer.parseInt(s.toString());
                    } catch (NumberFormatException e) {
                        data.reps = 0;
                    }
                }
            });

            binding.etWeight.addTextChangedListener(new SimpleTextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    try {
                        data.weightKg = Float.parseFloat(s.toString());
                    } catch (NumberFormatException e) {
                        data.weightKg = 0;
                    }
                }
            });
        }
    }

    static abstract class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {}
    }
}
