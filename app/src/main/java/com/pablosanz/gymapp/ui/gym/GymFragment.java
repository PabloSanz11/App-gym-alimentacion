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

import com.google.android.material.chip.Chip;
import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.Exercise;
import com.pablosanz.gymapp.data.model.ExerciseData;
import com.pablosanz.gymapp.data.repository.GymRepository;
import com.pablosanz.gymapp.databinding.FragmentGymBinding;

import java.util.List;

public class GymFragment extends Fragment {

    private FragmentGymBinding binding;
    private GymRepository gymRepository;
    private int selectedDayType = 1;

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

        binding.chipGroupDays.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) return;
            int id = checkedIds.get(0);
            if (id == R.id.chip_day1) selectedDayType = 1;
            else if (id == R.id.chip_day2) selectedDayType = 2;
            else if (id == R.id.chip_day3) selectedDayType = 3;
            else if (id == R.id.chip_day4) selectedDayType = 4;
            updateExerciseList();
        });

        binding.btnStartSession.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putInt("dayType", selectedDayType);
            Navigation.findNavController(v).navigate(R.id.action_gymFragment_to_workoutLogFragment, args);
        });

        binding.btnProgress.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_gymFragment_to_progressFragment));
    }

    private void loadSuggestedDay() {
        gymRepository.getLastSession(session -> {
            if (session == null) {
                selectedDayType = 1;
            } else {
                selectedDayType = (session.getDayType() % 4) + 1;
            }
            new Handler(Looper.getMainLooper()).post(() -> {
                selectChipForDay(selectedDayType);
                updateExerciseList();
            });
        });
    }

    private void selectChipForDay(int dayType) {
        int chipId;
        switch (dayType) {
            case 2: chipId = R.id.chip_day2; break;
            case 3: chipId = R.id.chip_day3; break;
            case 4: chipId = R.id.chip_day4; break;
            default: chipId = R.id.chip_day1; break;
        }
        Chip chip = binding.getRoot().findViewById(chipId);
        if (chip != null) chip.setChecked(true);
    }

    private void updateExerciseList() {
        String dayName = ExerciseData.getDayName(selectedDayType);
        binding.tvTodayDayTitle.setText("Día " + selectedDayType + " — " + dayName);

        List<Exercise> exercises = ExerciseData.getExercisesForDay(selectedDayType);
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
