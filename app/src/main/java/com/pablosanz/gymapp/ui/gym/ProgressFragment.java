package com.pablosanz.gymapp.ui.gym;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.pablosanz.gymapp.data.model.BodyMeasurement;
import com.pablosanz.gymapp.data.repository.GymRepository;
import com.pablosanz.gymapp.databinding.FragmentProgressBinding;
import com.pablosanz.gymapp.util.DateUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProgressFragment extends Fragment {

    private FragmentProgressBinding binding;
    private GymRepository gymRepository;
    private MeasurementAdapter measurementAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        gymRepository = new GymRepository(requireActivity().getApplication());

        binding.toolbarProgress.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).popBackStack());

        measurementAdapter = new MeasurementAdapter(new ArrayList<>());
        binding.rvMeasurements.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvMeasurements.setAdapter(measurementAdapter);

        setupChart();
        loadData();

        binding.btnSaveMeasurement.setOnClickListener(v -> saveMeasurement());
    }

    private void setupChart() {
        LineChart chart = binding.lineChart;
        chart.getDescription().setEnabled(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getAxisRight().setEnabled(false);
        chart.getLegend().setEnabled(true);
    }

    private void loadData() {
        gymRepository.getRecentMeasurements(30, measurements -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                updateChart(measurements);
                measurementAdapter.updateData(measurements);
            });
        });
    }

    private void updateChart(List<BodyMeasurement> measurements) {
        if (measurements == null || measurements.isEmpty()) {
            binding.lineChart.setNoDataText("Sin datos de peso aún");
            return;
        }

        List<BodyMeasurement> sorted = new ArrayList<>(measurements);
        Collections.sort(sorted, (a, b) -> a.getDate().compareTo(b.getDate()));

        List<Entry> entries = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            entries.add(new Entry(i, sorted.get(i).getWeightKg()));
        }

        LineDataSet dataSet = new LineDataSet(entries, "Peso corporal (kg)");
        dataSet.setColor(Color.parseColor("#2E7D32"));
        dataSet.setCircleColor(Color.parseColor("#2E7D32"));
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(4f);
        dataSet.setDrawValues(true);
        dataSet.setValueTextSize(10f);

        binding.lineChart.setData(new LineData(dataSet));
        binding.lineChart.invalidate();
    }

    private void saveMeasurement() {
        String weightStr = binding.etWeight.getText().toString().trim();
        String waistStr = binding.etWaist.getText().toString().trim();

        if (weightStr.isEmpty()) {
            Toast.makeText(getContext(), "Ingresa tu peso", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            float weight = Float.parseFloat(weightStr);
            float waist = waistStr.isEmpty() ? 0 : Float.parseFloat(waistStr);

            BodyMeasurement measurement = new BodyMeasurement(DateUtils.today(), weight, waist, "");
            gymRepository.insertMeasurement(measurement);

            binding.etWeight.setText("");
            binding.etWaist.setText("");
            Toast.makeText(getContext(), "Medición guardada", Toast.LENGTH_SHORT).show();
            loadData();
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Ingresa valores válidos", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
