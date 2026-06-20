package com.pablosanz.gymapp.ui.nutrition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.ShoppingListItem;
import com.pablosanz.gymapp.util.IngredientCategorizer;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_ITEM = 1;

    public interface OnItemToggled {
        void onToggled(ShoppingListItem item);
    }

    public interface OnItemEdit {
        void onEdit(ShoppingListItem item);
    }

    /** Cada elemento es un String (encabezado de categoría) o un ShoppingListItem. */
    private final List<Object> rows = new ArrayList<>();
    private final OnItemToggled toggleListener;
    private final OnItemEdit editListener;

    public ShoppingListAdapter(OnItemToggled toggleListener, OnItemEdit editListener) {
        this.toggleListener = toggleListener;
        this.editListener = editListener;
    }

    public void setItems(List<ShoppingListItem> items) {
        rows.clear();
        Map<String, List<ShoppingListItem>> grouped = new LinkedHashMap<>();
        for (String category : IngredientCategorizer.CATEGORY_ORDER) grouped.put(category, new ArrayList<>());
        for (ShoppingListItem item : items) {
            grouped.get(IngredientCategorizer.categorize(item.getIngredientName())).add(item);
        }
        for (Map.Entry<String, List<ShoppingListItem>> entry : grouped.entrySet()) {
            if (entry.getValue().isEmpty()) continue;
            rows.add(entry.getKey());
            rows.addAll(entry.getValue());
        }
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return rows.get(position) instanceof String ? TYPE_HEADER : TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_shopping_category_header, parent, false);
            return new HeaderViewHolder(v);
        }
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping_list, parent, false);
        return new ItemViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof HeaderViewHolder) {
            ((HeaderViewHolder) holder).tvHeader.setText((String) rows.get(position));
            return;
        }
        ShoppingListItem item = (ShoppingListItem) rows.get(position);
        ItemViewHolder h = (ItemViewHolder) holder;

        h.tvName.setText(item.getIngredientName());
        h.tvQty.setText(String.format("%.0fg", item.getTotalQuantityG()));
        h.tvCost.setText(item.getEstimatedCostMxn() > 0
                ? String.format("$%.0f", item.getEstimatedCostMxn()) : "—");

        if (item.getStore() != null) {
            h.tvStore.setText(item.getStore());
            h.tvStore.setVisibility(View.VISIBLE);
        } else {
            h.tvStore.setVisibility(View.GONE);
        }

        h.checkBox.setOnCheckedChangeListener(null);
        h.checkBox.setChecked(item.isPurchased());
        h.checkBox.setOnCheckedChangeListener((btn, checked) -> {
            item.setPurchased(checked);
            if (toggleListener != null) toggleListener.onToggled(item);
        });

        h.btnEdit.setOnClickListener(v -> {
            if (editListener != null) editListener.onEdit(item);
        });
    }

    @Override
    public int getItemCount() { return rows.size(); }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvHeader;
        HeaderViewHolder(@NonNull View v) {
            super(v);
            tvHeader = (TextView) v;
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvName, tvQty, tvCost, tvStore, btnEdit;

        ItemViewHolder(@NonNull View v) {
            super(v);
            checkBox = v.findViewById(R.id.cb_shopping_item);
            tvName = v.findViewById(R.id.tv_shopping_item_name);
            tvQty = v.findViewById(R.id.tv_shopping_item_qty);
            tvCost = v.findViewById(R.id.tv_shopping_item_cost);
            tvStore = v.findViewById(R.id.tv_shopping_item_store);
            btnEdit = v.findViewById(R.id.btn_edit_shopping_item);
        }
    }
}
