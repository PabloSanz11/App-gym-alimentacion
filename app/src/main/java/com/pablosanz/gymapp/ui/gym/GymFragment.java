package com.pablosanz.gymapp.ui.gym;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.pablosanz.gymapp.R;
import com.pablosanz.gymapp.data.model.ExerciseData;
import com.pablosanz.gymapp.data.repository.GymRepository;
import com.pablosanz.gymapp.databinding.FragmentGymBinding;

public class GymFragment extends Fragment {

    private FragmentGymBinding binding;
    private GymRepository gymRepository;
    private DayCardAdapter dayCardAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentGymBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        gymRepository = new GymRepository(requireActivity().getApplication());

        dayCardAdapter = new DayCardAdapter(dayType -> {
            Bundle args = new Bundle();
            args.putInt("dayType", dayType);
            Navigation.findNavController(requireView())
                    .navigate(R.id.action_gymFragment_to_workoutLogFragment, args);
        });

        // Peek carousel setup
        binding.viewPagerDays.setAdapter(dayCardAdapter);
        binding.viewPagerDays.setOffscreenPageLimit(1);

        // Scale + fade transformer for peek effect
        CompositePageTransformer transformer = new CompositePageTransformer();
        transformer.addTransformer(new MarginPageTransformer(
                (int)(12 * getResources().getDisplayMetrics().density)));
        transformer.addTransformer((page, position) -> {
            float scale = 1f - 0.08f * Math.abs(position);
            page.setScaleY(scale);
            page.setAlpha(0.65f + 0.35f * (1 - Math.abs(position)));
        });
        binding.viewPagerDays.setPageTransformer(transformer);

        binding.viewPagerDays.registerOnPageChangeCallback(
                new ViewPager2.OnPageChangeCallback() {
                    @Override
                    public void onPageSelected(int position) {
                        updateDots(position);
                    }
                });

        binding.btnProgress.setOnClickListener(v ->
                Navigation.findNavController(v)
                        .navigate(R.id.action_gymFragment_to_progressFragment));

        loadSuggestedDay();
        buildDots(4);
    }

    private void loadSuggestedDay() {
        gymRepository.getLastSession(session -> {
            int suggested = (session == null) ? 1 : (session.getDayType() % 4) + 1;
            new Handler(Looper.getMainLooper()).post(() -> {
                if (binding == null) return;
                String name = ExerciseData.getDayName(suggested);
                binding.tvSuggestedLabel.setText(
                        "⚡ Siguiente: Día " + suggested + "  ·  " + name);
                dayCardAdapter.setSuggestedDay(suggested);
                // Scroll to suggested day
                binding.viewPagerDays.setCurrentItem(suggested - 1, false);
                updateDots(suggested - 1);
            });
        });
    }

    private void buildDots(int count) {
        if (binding == null) return;
        binding.layoutDayDots.removeAllViews();
        float dp = getResources().getDisplayMetrics().density;
        int size = (int)(8 * dp);
        int margin = (int)(5 * dp);
        for (int i = 0; i < count; i++) {
            View dot = new View(getContext());
            android.widget.LinearLayout.LayoutParams p =
                    new android.widget.LinearLayout.LayoutParams(size, size);
            p.setMargins(margin, 0, margin, 0);
            dot.setLayoutParams(p);
            dot.setBackgroundColor(Color.parseColor("#C7C7CC"));
            // Round dots via radius
            dot.setBackground(makeCircle("#C7C7CC"));
            binding.layoutDayDots.addView(dot);
        }
        updateDots(0);
    }

    private void updateDots(int selected) {
        if (binding == null) return;
        float dp = getResources().getDisplayMetrics().density;
        int[] colors = {0xFF1B3A6B, 0xFF16A34A, 0xFF7C3AED, 0xFFEA580C};
        for (int i = 0; i < binding.layoutDayDots.getChildCount(); i++) {
            View dot = binding.layoutDayDots.getChildAt(i);
            android.widget.LinearLayout.LayoutParams p =
                    (android.widget.LinearLayout.LayoutParams) dot.getLayoutParams();
            boolean active = (i == selected);
            p.width = active ? (int)(24 * dp) : (int)(8 * dp);
            dot.setLayoutParams(p);
            int color = active ? colors[Math.min(i, colors.length - 1)] : 0xFFC7C7CC;
            dot.setBackground(makeRoundRect(color, active ? 4 : 50));
        }
    }

    private android.graphics.drawable.GradientDrawable makeCircle(String hex) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.OVAL);
        gd.setColor(Color.parseColor(hex));
        return gd;
    }

    private android.graphics.drawable.GradientDrawable makeRoundRect(int color, int radiusDp) {
        android.graphics.drawable.GradientDrawable gd = new android.graphics.drawable.GradientDrawable();
        gd.setShape(android.graphics.drawable.GradientDrawable.RECTANGLE);
        gd.setColor(color);
        float dp = getResources().getDisplayMetrics().density;
        gd.setCornerRadius(radiusDp * dp);
        return gd;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
