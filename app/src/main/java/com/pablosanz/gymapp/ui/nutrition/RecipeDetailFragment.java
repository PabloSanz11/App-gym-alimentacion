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
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.databinding.FragmentRecipeDetailBinding;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import com.pablosanz.gymapp.data.repository.NutritionRepository;

import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private FragmentRecipeDetailBinding binding;
    private NutritionRepository nutritionRepository;
    private RecipeIngredientAdapter ingredientAdapter;
    private long recipeId;
    private String recipeName;
    private String mealSlot;
    private String date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeDetailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            recipeId = getArguments().getLong("recipeId");
            recipeName = getArguments().getString("recipeName", "Platillo");
            mealSlot = getArguments().getString("mealSlot", "almuerzo");
            date = getArguments().getString("date");
        }
        nutritionRepository = new NutritionRepository(requireActivity().getApplication());

        binding.toolbarRecipeDetail.setTitle(recipeName);
        binding.toolbarRecipeDetail.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());
        binding.tvRecipeSubtitle.setText("Activa o desactiva ingredientes según tu preparación");

        ingredientAdapter = new RecipeIngredientAdapter();
        ingredientAdapter.setOnTotalsChangedListener(() ->
                updateTotals(ingredientAdapter.getItems()));
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvIngredients.setAdapter(ingredientAdapter);

        nutritionRepository.getRecipeIngredients(recipeId, ingredients ->
                requireActivity().runOnUiThread(() -> {
                    ingredientAdapter.setItems(ingredients);
                    updateTotals(ingredients);
                }));

        binding.btnAddRecipe.setOnClickListener(v -> addRecipeToMeal());
    }

    private void updateTotals(List<RecipeIngredient> ingredients) {
        float cal = 0, prot = 0, carbs = 0, fat = 0;
        for (RecipeIngredient i : ingredients) {
            if (i.isIncluded()) {
                cal += i.getCaloriesKcal();
                prot += i.getProteinG();
                carbs += i.getCarbsG();
                fat += i.getFatG();
            }
        }
        binding.tvTotals.setText(String.format("%.0f kcal  ·  P: %.0fg  ·  C: %.0fg  ·  G: %.0fg",
                cal, prot, carbs, fat));
    }

    private void addRecipeToMeal() {
        List<RecipeIngredient> items = ingredientAdapter.getItems();
        float totalCal = 0, totalProt = 0, totalCarbs = 0, totalFat = 0;
        for (RecipeIngredient i : items) {
            if (i.isIncluded()) {
                totalCal += i.getCaloriesKcal();
                totalProt += i.getProteinG();
                totalCarbs += i.getCarbsG();
                totalFat += i.getFatG();
            }
        }
        final float fCal = totalCal, fProt = totalProt, fCarbs = totalCarbs, fFat = totalFat;
        nutritionRepository.getOrCreateMealLog(date, mealSlot, mealLog -> {
            FoodEntry entry = new FoodEntry(mealLog.getId(), recipeName, "",
                    fProt, fCarbs, fCal, fFat, 0f);
            nutritionRepository.insertFoodEntry(entry);
            mealLog.setTotalCaloriesKcal(mealLog.getTotalCaloriesKcal() + fCal);
            mealLog.setTotalProteinG(mealLog.getTotalProteinG() + fProt);
            mealLog.setTotalCarbsG(mealLog.getTotalCarbsG() + fCarbs);
            mealLog.setTotalFatG(mealLog.getTotalFatG() + fFat);
            nutritionRepository.updateMealLog(mealLog);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), recipeName + " agregado al " + mealSlot, Toast.LENGTH_SHORT).show();
                Navigation.findNavController(requireView()).popBackStack();
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
