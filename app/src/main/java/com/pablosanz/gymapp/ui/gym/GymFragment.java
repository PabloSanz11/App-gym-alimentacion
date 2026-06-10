package com.pablosanz.gymapp.ui.gym;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.Exercise;
import com.pablosanz.gymapp.data.model.ExerciseData;
import com.pablosanz.gymapp.data.model.WorkoutSession;
import com.pablosanz.gymapp.data.repository.GymRepository;
import com.pablosanz.gymapp.databinding.FragmentGymBinding;

import java.util.List;

public class GymFragment extends Fragment {

    private FragmentGymBinding binding;
    private GymRepository gymRepository;
    private int suggestedDayType = 1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentGymBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gymRepository = new GymRepository(requireActivity().getApplication());

        loadSuggestedDay();

        binding.btnStartSession.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("dayType", suggestedDayType);
            Navigation.findNavController(v).navigate(R.id.action_gymFragment_to_workoutLogFragment, args);
        });

        binding.btnProgress.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_gymFragment_to_progressFragment));
    }

    private void loadSuggestedDay() {
        gymRepository.getLastSession(session -> {
            if (session == null) {
                suggestedDayType = 1;
            } else {
                suggestedDayType = (session.getDayType() % 4) + 1;
            }

            new Handler(Looper.getMainLooper()).post(() -> updateUI());
        });

        gymRepository.getRecentMeasurements(1, measurements -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                if (measurements != null && !measurements.isEmpty()) {
                    float weight = measurements.get(0).getWeightKg();
                    binding.tvLastWeight.setText(String.format("%.1f kg", weight));
                } else {
                    binding.tvLastWeight.setText("-- kg");
                }
            });
        });
    }

    private void updateUI() {
        String dayName = ExerciseData.getDayName(suggestedDayType);
        binding.tvTodayDayTitle.setText("Día " + suggestedDayType + ": " + dayName);

        List<Exercise> exercises = ExerciseData.getExercisesForDay(suggestedDayType);
        GymExercisePreviewAdapter adapter = new GymExercisePreviewAdapter(exercises);
        binding.rvTodayExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTodayExercises.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
