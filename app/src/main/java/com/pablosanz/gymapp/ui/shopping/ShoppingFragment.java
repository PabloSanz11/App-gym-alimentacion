package com.pablosanz.gymapp.ui.shopping;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pablosanz.gymapp.data.model.ShoppingListItem;
import com.pablosanz.gymapp.data.repository.NutritionRepository;
import com.pablosanz.gymapp.databinding.FragmentShoppingBinding;
import com.pablosanz.gymapp.ui.nutrition.ShoppingItemEditDialog;
import com.pablosanz.gymapp.ui.nutrition.ShoppingListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShoppingFragment extends Fragment {

    private FragmentShoppingBinding binding;
    private NutritionRepository nutritionRepository;
    private List<ShoppingListItem> items = new ArrayList<>();
    private ShoppingListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShoppingBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        nutritionRepository = new NutritionRepository(requireActivity().getApplication());

        adapter = new ShoppingListAdapter(
                item -> updateSummary(),
                item -> ShoppingItemEditDialog.show(
                        requireContext(), getLayoutInflater(), nutritionRepository, item, saved -> {
                            adapter.setItems(items);
                            updateSummary();
                        }));
        binding.rvShopping.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvShopping.setAdapter(adapter);

        binding.btnResetShopping.setOnClickListener(v -> resetAll());
        binding.btnSaveShopping.setOnClickListener(v -> saveAll());

        loadShoppingList();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadShoppingList();
    }

    private void loadShoppingList() {
        nutritionRepository.getShoppingList(loaded -> requireActivity().runOnUiThread(() -> {
            if (binding == null) return;
            items.clear();
            items.addAll(loaded);
            adapter.setItems(items);
            updateSummary();
        }));
    }

    private void saveAll() {
        nutritionRepository.updateShoppingItems(new ArrayList<>(items), () -> requireActivity().runOnUiThread(() -> {
            if (getContext() == null) return;
            android.widget.Toast.makeText(getContext(), "Lista guardada", android.widget.Toast.LENGTH_SHORT).show();
        }));
    }

    private void updateSummary() {
        if (binding == null) return;
        boolean empty = items.isEmpty();
        binding.tvShoppingEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.rvShopping.setVisibility(empty ? View.GONE : View.VISIBLE);

        int checked = 0;
        for (ShoppingListItem item : items) {
            if (item.isPurchased()) checked++;
        }
        binding.tvShoppingTotal.setText(items.size() + " ingredientes");
        binding.tvShoppingProgress.setText(checked + " / " + items.size() + " comprados");
        int pct = items.isEmpty() ? 0 : (checked * 100 / items.size());
        binding.progressShopping.setProgress(pct);

        float totalCost = 0f;
        for (ShoppingListItem item : items) {
            totalCost += item.getEstimatedCostMxn();
        }
        binding.tvShoppingTotalCost.setText(String.format("$%.0f", totalCost));
    }

    private void resetAll() {
        for (ShoppingListItem item : items) {
            item.setPurchased(false);
        }
        nutritionRepository.updateShoppingItems(new ArrayList<>(items), null);
        adapter.setItems(items);
        updateSummary();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
