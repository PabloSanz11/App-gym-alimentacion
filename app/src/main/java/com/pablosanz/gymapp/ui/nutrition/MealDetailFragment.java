package com.pablosanz.gymapp.ui.nutrition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentMealDetailBinding;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.ArrayList;
import java.util.List;

public class MealDetailFragment extends Fragment {

    private FragmentMealDetailBinding binding;
    private NutritionRepository repo;
    private FoodEntryAdapter adapter;
    private final List<FoodEntry> entries = new ArrayList<>();

    private String mealSlot;
    private String date;
    private long mealLogId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentMealDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mealSlot = getArguments().getString("mealSlot", "desayuno");
            date = getArguments().getString("date", DateUtils.today());
            mealLogId = getArguments().getLong("mealLogId", -1L);
        }

        repo = new NutritionRepository(requireActivity().getApplication());

        String title = capitalize(mealSlot) + "  ·  " + DateUtils.formatForDisplay(date);
        binding.toolbarMealDetail.setTitle(title);
        binding.toolbarMealDetail.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        adapter = new FoodEntryAdapter(entries,
                entry -> deleteEntry(entry),
                (entry, newQty) -> editEntry(entry, newQty));

        binding.rvFoodEntries.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvFoodEntries.addItemDecoration(
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        binding.rvFoodEntries.setAdapter(adapter);

        binding.fabAddFood.setOnClickListener(v -> {
            Bundle args = new Bundle();
            args.putString("mealSlot", mealSlot);
            args.putString("date", date);
            Navigation.findNavController(v)
                    .navigate(R.id.action_mealDetailFragment_to_mealLogFragment, args);
        });

        loadEntries();
    }

    private void loadEntries() {
        if (mealLogId < 0) {
            // No log yet — empty state
            showEmpty(true);
            return;
        }
        repo.getFoodEntriesByMealLog(mealLogId, result ->
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    adapter.setItems(result);
                    showEmpty(result.isEmpty());
                    updateSummary(result);
                }));
    }

    private void deleteEntry(FoodEntry entry) {
        repo.deleteFoodEntry(entry, () ->
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    Toast.makeText(getContext(), "Eliminado", Toast.LENGTH_SHORT).show();
                    loadEntries();
                }));
    }

    private void editEntry(FoodEntry entry, float newQty) {
        repo.updateFoodEntryQuantity(entry, newQty, () ->
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    loadEntries();
                }));
    }

    private void updateSummary(List<FoodEntry> list) {
        float cal = 0, p = 0, c = 0, f = 0;
        for (FoodEntry e : list) {
            cal += e.getCaloriesKcal();
            p += e.getProteinG();
            c += e.getCarbsG();
            f += e.getFatG();
        }
        binding.tvDetailCalories.setText(String.format("%.0f", cal));
        binding.tvDetailProtein.setText(String.format("%.0f", p));
        binding.tvDetailCarbs.setText(String.format("%.0f", c));
        binding.tvDetailFat.setText(String.format("%.0f", f));
    }

    private void showEmpty(boolean empty) {
        binding.tvEmptyEntries.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.rvFoodEntries.setVisibility(empty ? View.GONE : View.VISIBLE);
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return s;
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
