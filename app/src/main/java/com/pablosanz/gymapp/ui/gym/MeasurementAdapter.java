package com.pablosanz.gymapp.ui.gym;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.BodyMeasurement;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.List;

public class MeasurementAdapter extends RecyclerView.Adapter<MeasurementAdapter.ViewHolder> {

    private List<BodyMeasurement> measurements;

    public MeasurementAdapter(List<BodyMeasurement> measurements) {
        this.measurements = measurements;
    }

    public void updateData(List<BodyMeasurement> newData) {
        this.measurements = newData;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_measurement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BodyMeasurement m = measurements.get(position);
        holder.tvDate.setText(DateUtils.formatForDisplay(m.getDate()));
        holder.tvWeight.setText(String.format("%.1f kg", m.getWeightKg()));
        if (m.getWaistCm() > 0) {
            holder.tvWaist.setText(String.format("Cintura: %.1f cm", m.getWaistCm()));
            holder.tvWaist.setVisibility(View.VISIBLE);
        } else {
            holder.tvWaist.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return measurements.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDate, tvWeight, tvWaist;

        ViewHolder(View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.tv_measurement_date);
            tvWeight = itemView.findViewById(R.id.tv_measurement_weight);
            tvWaist = itemView.findViewById(R.id.tv_measurement_waist);
        }
    }
}
