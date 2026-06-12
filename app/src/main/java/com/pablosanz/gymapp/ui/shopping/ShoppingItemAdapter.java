package com.pablosanz.gymapp.ui.shopping;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;

import java.util.List;

public class ShoppingItemAdapter extends RecyclerView.Adapter<ShoppingItemAdapter.ViewHolder> {

    public interface OnCheckChangeListener {
        void onChanged();
    }

    private final List<ShoppingItem> items;
    private final OnCheckChangeListener listener;

    public ShoppingItemAdapter(List<ShoppingItem> items, OnCheckChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shopping, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        ShoppingItem item = items.get(position);

        // Category header: show if first item in category
        boolean showHeader = position == 0 ||
                !items.get(position - 1).category.equals(item.category);
        if (showHeader) {
            h.tvCategoryHeader.setVisibility(View.VISIBLE);
            h.tvCategoryHeader.setText(item.category);
        } else {
            h.tvCategoryHeader.setVisibility(View.GONE);
        }

        h.cbItem.setOnCheckedChangeListener(null);
        h.cbItem.setChecked(item.checked);
        h.tvName.setText(item.name);
        h.tvQty.setText(item.quantity);
        h.tvPrice.setText(String.format("$%.0f", item.priceEstimated));

        applyStrikeThrough(h, item.checked);

        h.cbItem.setOnCheckedChangeListener((btn, checked) -> {
            item.checked = checked;
            applyStrikeThrough(h, checked);
            listener.onChanged();
        });
    }

    private void applyStrikeThrough(ViewHolder h, boolean checked) {
        int flags = checked ? Paint.STRIKE_THRU_TEXT_FLAG : 0;
        h.tvName.setPaintFlags((h.tvName.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG) | flags);
        h.tvName.setAlpha(checked ? 0.4f : 1f);
        h.tvQty.setAlpha(checked ? 0.4f : 1f);
        h.tvPrice.setAlpha(checked ? 0.4f : 1f);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryHeader, tvName, tvQty, tvPrice;
        CheckBox cbItem;

        ViewHolder(@NonNull View v) {
            super(v);
            tvCategoryHeader = v.findViewById(R.id.tv_category_header);
            cbItem = v.findViewById(R.id.cb_item);
            tvName = v.findViewById(R.id.tv_item_name);
            tvQty = v.findViewById(R.id.tv_item_qty);
            tvPrice = v.findViewById(R.id.tv_item_price);
        }
    }
}
