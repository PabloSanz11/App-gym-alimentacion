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
import com.pablosanz.gymapp.data.model.ExerciseData;
import com.pablosanz.gymapp.data.repository.GymRepository;
import com.pablosanz.gymapp.databinding.FragmentGymBinding;

public class GymFragment extends Fragment {

    private FragmentGymBinding binding;
    private GymRepository gymRepository;
    private DayCardAdapter dayCardAdapter;

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

        dayCardAdapter = new DayCardAdapter(dayType -> {
            Bundle args = new Bundle();
            args.putInt("dayType", dayType);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_gymFragment_to_workoutLogFragment, args);
        });

        binding.rvDayCards.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvDayCards.setAdapter(dayCardAdapter);

        binding.btnProgress.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_gymFragment_to_progressFragment));

        loadSuggestedDay();
    }

    private void loadSuggestedDay() {
        gymRepository.getLastSession(session -> {
            int suggested = (session == null) ? 1 : (session.getDayType() % 4) + 1;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (binding == null) return;
                String name = ExerciseData.getDayName(suggested);
                binding.tvSuggestedLabel.setText("Siguiente: Día " + suggested + " · " + name);
                dayCardAdapter.setSuggestedDay(suggested);
            });
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
