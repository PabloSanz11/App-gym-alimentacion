package com.pablosanz.gymapp.ui.nutrition;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.Recipe;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentRecipeListBinding;

public class RecipeListFragment extends Fragment {

    private FragmentRecipeListBinding binding;
    private NutritionRepository nutritionRepository;
    private RecipeAdapter recipeAdapter;
    private String mealSlot;
    private String date;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecipeListBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (getArguments() != null) {
            mealSlot = getArguments().getString("mealSlot", "almuerzo");
            date = getArguments().getString("date");
        }
        nutritionRepository = new NutritionRepository(requireActivity().getApplication());
        binding.toolbarRecipes.setTitle("Platillos — " + capitalize(mealSlot));
        binding.toolbarRecipes.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        recipeAdapter = new RecipeAdapter(recipe -> {
            Bundle args = new Bundle();
            args.putLong("recipeId", recipe.getId());
            args.putString("recipeName", recipe.getName());
            args.putString("mealSlot", mealSlot);
            args.putString("date", date);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_recipeListFragment_to_recipeDetailFragment, args);
        });
        binding.rvRecipes.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvRecipes.setAdapter(recipeAdapter);
        loadRecipes("");

        binding.etRecipeSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void afterTextChanged(Editable s) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                loadRecipes(s.toString().trim());
            }
        });
    }

    private void loadRecipes(String query) {
        nutritionRepository.searchRecipes(query, recipes ->
                requireActivity().runOnUiThread(() -> recipeAdapter.setItems(recipes)));
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
