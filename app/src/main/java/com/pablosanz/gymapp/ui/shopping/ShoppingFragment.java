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

        adapter = new ShoppingListAdapter(items, item -> {
            nutritionRepository.updateShoppingItem(item);
            updateSummary();
        });
        binding.rvShopping.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvShopping.setAdapter(adapter);

        binding.btnResetShopping.setOnClickListener(v -> resetAll());

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
            adapter.notifyDataSetChanged();
            updateSummary();
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
    }

    private void resetAll() {
        for (ShoppingListItem item : items) {
            item.setPurchased(false);
            nutritionRepository.updateShoppingItem(item);
        }
        adapter.notifyDataSetChanged();
        updateSummary();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
