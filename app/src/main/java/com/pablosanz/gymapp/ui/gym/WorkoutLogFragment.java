package com.pablosanz.gymapp.ui.gym;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.data.model.Exercise;
import com.pablosanz.gymapp.data.model.ExerciseData;
import com.pablosanz.gymapp.data.model.ExerciseLog;
import com.pablosanz.gymapp.data.model.WorkoutSession;
import com.pablosanz.gymapp.data.repository.GymRepository;
import com.pablosanz.gymapp.databinding.FragmentWorkoutLogBinding;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WorkoutLogFragment extends Fragment {

    private FragmentWorkoutLogBinding binding;
    private GymRepository gymRepository;
    private ExerciseSetAdapter adapter;
    private int dayType = 1;
    private List<Exercise> exercises;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentWorkoutLogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            dayType = getArguments().getInt("dayType", 1);
        }

        gymRepository = new GymRepository(requireActivity().getApplication());
        exercises = ExerciseData.getExercisesForDay(dayType);

        String dayName = ExerciseData.getDayName(dayType);
        binding.toolbarWorkout.setTitle("Día " + dayType + ": " + dayName);
        binding.toolbarWorkout.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        List<String> names = new ArrayList<>();
        for (Exercise e : exercises) names.add(e.getName());

        gymRepository.getLastWeightForExercises(names, lastWeights -> {
            requireActivity().runOnUiThread(() -> {
                adapter = new ExerciseSetAdapter(exercises, lastWeights);
                binding.rvExerciseSets.setLayoutManager(new LinearLayoutManager(getContext()));
                binding.rvExerciseSets.setAdapter(adapter);
            });
        });

        binding.fabSaveSession.setOnClickListener(v -> saveSession());
    }

    private void saveSession() {
        WorkoutSession session = new WorkoutSession(DateUtils.today(), dayType, 0, "");
        gymRepository.insertSession(session, sessionId -> {
            List<ExerciseSetAdapter.ExerciseSetData> items = adapter.getItems();
            for (ExerciseSetAdapter.ExerciseSetData data : items) {
                if (data.reps > 0 || data.weightKg > 0) {
                    ExerciseLog log = new ExerciseLog(sessionId, data.exercise.getName(),
                            data.setNumber, data.reps, data.weightKg, "");
                    gymRepository.insertExerciseLog(log);
                }
            }
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), "¡Entrenamiento guardado!", Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
