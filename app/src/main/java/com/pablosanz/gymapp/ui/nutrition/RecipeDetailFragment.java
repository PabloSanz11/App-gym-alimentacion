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

import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentRecipeDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private FragmentRecipeDetailBinding binding;
    private NutritionRepository nutritionRepository;
    private long recipeId;
    private String mealSlot;
    private String date;
    private Recipe recipe;
    private List<RecipeIngredient> editableIngredients = new ArrayList<>();
    private EditableIngredientAdapter ingredientAdapter;

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

        Bundle args = getArguments();
        if (args != null) {
            recipeId = args.getLong("recipeId", 0L);
            mealSlot = args.getString("mealSlot", "desayuno");
            date = args.getString("date", "");
        }

        nutritionRepository = new NutritionRepository(requireActivity().getApplication());

        binding.toolbarRecipeDetail.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        ingredientAdapter = new EditableIngredientAdapter(editableIngredients, this::onIngredientChanged);
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvIngredients.setAdapter(ingredientAdapter);

        binding.btnAddIngredient.setOnClickListener(v -> {
            RecipeIngredient blank = new RecipeIngredient(recipeId, "Nuevo ingrediente", 100, 0, 0, 0, 0);
            editableIngredients.add(blank);
            ingredientAdapter.notifyItemInserted(editableIngredients.size() - 1);
        });

        loadRecipeDetail();
    }

    private void loadRecipeDetail() {
        nutritionRepository.getRecipeById(recipeId, r -> {
            if (r == null || getActivity() == null) return;
            recipe = r;
            nutritionRepository.getRecipeIngredients(recipeId, ingredients -> {
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    editableIngredients.clear();
                    editableIngredients.addAll(ingredients);
                    binding.toolbarRecipeDetail.setTitle(recipe.getName());
                    ingredientAdapter.notifyDataSetChanged();
                    updateMacroSummary();
                });
            });
        });
    }

    private void onIngredientChanged() {
        requireActivity().runOnUiThread(this::updateMacroSummary);
    }

    private void updateMacroSummary() {
        float protein = 0, carbs = 0, cals = 0, fat = 0;
        for (RecipeIngredient ing : editableIngredients) {
            protein += ing.getProteinG();
            carbs += ing.getCarbsG();
            cals += ing.getCaloriesKcal();
            fat += ing.getFatG();
        }
        binding.tvRecipeCalories.setText(String.format("%.0f kcal", cals));
        binding.tvRecipeProtein.setText(String.format("P: %.0fg", protein));
        binding.tvRecipeCarbs.setText(String.format("C: %.0fg", carbs));
        binding.tvRecipeFat.setText(String.format("G: %.0fg", fat));

        final float fProtein = protein, fCarbs = carbs, fCals = cals, fFat = fat;
        binding.btnAddRecipe.setOnClickListener(v -> addToMeal(fProtein, fCarbs, fCals, fFat));
    }

    private void addToMeal(float protein, float carbs, float cals, float fat) {
        String name = recipe != null ? recipe.getName() : "Platillo";
        nutritionRepository.getOrCreateMealLog(date, mealSlot, mealLog -> {
            FoodEntry entry = new FoodEntry(
                    mealLog.getId(), name, "",
                    protein, carbs, cals, fat, 0);
            nutritionRepository.insertFoodEntry(entry);
            mealLog.setTotalProteinG(mealLog.getTotalProteinG() + protein);
            mealLog.setTotalCarbsG(mealLog.getTotalCarbsG() + carbs);
            mealLog.setTotalCaloriesKcal(mealLog.getTotalCaloriesKcal() + cals);
            mealLog.setTotalFatG(mealLog.getTotalFatG() + fat);
            nutritionRepository.updateMealLog(mealLog);
            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), name + " agregado", Toast.LENGTH_SHORT).show();
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
