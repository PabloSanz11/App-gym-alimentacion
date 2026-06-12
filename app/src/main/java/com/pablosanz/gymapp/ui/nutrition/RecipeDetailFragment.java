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

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.FoodEntry;
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.model.RecipeIngredient;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentRecipeDetailBinding;

import java.util.List;

public class RecipeDetailFragment extends Fragment {

    private FragmentRecipeDetailBinding binding;
    private NutritionRepository nutritionRepository;
    private long recipeId;
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

        Bundle args = getArguments();
        if (args != null) {
            recipeId = args.getLong("recipeId", 0L);
            mealSlot = args.getString("mealSlot", "desayuno");
            date = args.getString("date", "");
        }

        nutritionRepository = new NutritionRepository(requireActivity().getApplication());

        binding.toolbarRecipeDetail.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        loadRecipeDetail();
    }

    private void loadRecipeDetail() {
        nutritionRepository.getRecipeById(recipeId, recipe -> {
            if (recipe == null || getActivity() == null) return;
            nutritionRepository.getRecipeIngredients(recipeId, ingredients -> {
                requireActivity().runOnUiThread(() -> {
                    if (binding == null) return;
                    displayRecipe(recipe, ingredients);
                });
            });
        });
    }

    private void displayRecipe(Recipe recipe, List<RecipeIngredient> ingredients) {
        binding.toolbarRecipeDetail.setTitle(recipe.getName());
        binding.tvRecipeCalories.setText(String.format("%.0f kcal", recipe.getTotalCaloriesKcal()));
        binding.tvRecipeProtein.setText(String.format("P: %.0fg", recipe.getTotalProteinG()));
        binding.tvRecipeCarbs.setText(String.format("C: %.0fg", recipe.getTotalCarbsG()));
        binding.tvRecipeFat.setText(String.format("G: %.0fg", recipe.getTotalFatG()));

        RecipeIngredientAdapter adapter = new RecipeIngredientAdapter(ingredients);
        binding.rvIngredients.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvIngredients.setAdapter(adapter);

        binding.btnAddRecipe.setOnClickListener(v -> addRecipeToMeal(recipe));
    }

    private void addRecipeToMeal(Recipe recipe) {
        nutritionRepository.getOrCreateMealLog(date, mealSlot, mealLog -> {
            FoodEntry entry = new FoodEntry(
                    mealLog.getId(),
                    recipe.getName(),
                    "",
                    recipe.getTotalProteinG(),
                    recipe.getTotalCarbsG(),
                    recipe.getTotalCaloriesKcal(),
                    recipe.getTotalFatG(),
                    0);
            nutritionRepository.insertFoodEntry(entry);

            mealLog.setTotalProteinG(mealLog.getTotalProteinG() + recipe.getTotalProteinG());
            mealLog.setTotalCarbsG(mealLog.getTotalCarbsG() + recipe.getTotalCarbsG());
            mealLog.setTotalCaloriesKcal(mealLog.getTotalCaloriesKcal() + recipe.getTotalCaloriesKcal());
            mealLog.setTotalFatG(mealLog.getTotalFatG() + recipe.getTotalFatG());
            nutritionRepository.updateMealLog(mealLog);

            requireActivity().runOnUiThread(() -> {
                Toast.makeText(getContext(), recipe.getName() + " agregado", Toast.LENGTH_SHORT).show();
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
