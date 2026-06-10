package com.pablosanz.gymapp.ui.gym;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.Exercise;

import java.util.List;

public class GymExercisePreviewAdapter extends RecyclerView.Adapter<GymExercisePreviewAdapter.ViewHolder> {

    private final List<Exercise> exercises;

    public GymExercisePreviewAdapter(List<Exercise> exercises) {
        this.exercises = exercises;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_exercise_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Exercise exercise = exercises.get(position);
        holder.tvName.setText(exercise.getName());
        holder.tvSetsReps.setText(exercise.getSetsTarget() + "x" + exercise.getRepsTarget());
    }

    @Override
    public int getItemCount() {
        return exercises.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvSetsReps;

        ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_exercise_name);
            tvSetsReps = itemView.findViewById(R.id.tv_sets_reps);
        }
    }
}
