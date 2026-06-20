package com.pablosanz.gymapp.ui.nutrition;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.ShoppingListItem;

import java.util.List;

public class ShoppingListAdapter extends RecyclerView.Adapter<ShoppingListAdapter.ViewHolder> {

    public interface OnItemToggled {
        void onToggled(ShoppingListItem item);
    }

    private final List<ShoppingListItem> items;
    private final OnItemToggled listener;

    public ShoppingListAdapter(List<ShoppingListItem> items, OnItemToggled listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ShoppingListItem item = items.get(position);
        h.tvName.setText(item.getIngredientName());
        h.tvQty.setText(String.format("%.0fg", item.getTotalQuantityG()));
        h.checkBox.setOnCheckedChangeListener(null);
        h.checkBox.setChecked(item.isPurchased());
        h.checkBox.setOnCheckedChangeListener((btn, checked) -> {
            item.setPurchased(checked);
            listener.onToggled(item);
        });

        if (h.costWatcher != null) h.etCost.removeTextChangedListener(h.costWatcher);
        h.etCost.setText(item.getEstimatedCostMxn() > 0
                ? trimTrailingZero(item.getEstimatedCostMxn()) : "");
        h.costWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                float cost;
                try {
                    cost = s.length() > 0 ? Float.parseFloat(s.toString()) : 0f;
                } catch (NumberFormatException e) {
                    return;
                }
                item.setEstimatedCostMxn(cost);
                listener.onToggled(item);
            }
        };
        h.etCost.addTextChangedListener(h.costWatcher);
    }

    private String trimTrailingZero(float value) {
        return value == Math.floor(value) ? String.valueOf((int) value) : String.valueOf(value);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvName, tvQty;
        EditText etCost;
        TextWatcher costWatcher;

        ViewHolder(@NonNull View v) {
            super(v);
            checkBox = v.findViewById(R.id.cb_shopping_item);
            tvName = v.findViewById(R.id.tv_shopping_item_name);
            tvQty = v.findViewById(R.id.tv_shopping_item_qty);
            etCost = v.findViewById(R.id.et_shopping_item_cost);
        }
    }
}
