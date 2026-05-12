package com.example.medisafe.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.example.medisafe.MainActivity;
import com.example.medisafe.R;
import com.example.medisafe.databinding.ActivityOnboardingBinding;
import com.example.medisafe.databinding.OnboardingSlideBinding;
import com.example.medisafe.utils.PreferenceManager;
import com.google.android.material.tabs.TabLayoutMediator;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private ViewPager2 viewPager;
    private OnboardingAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        adapter = new OnboardingAdapter();
        viewPager = binding.viewPager;
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.indicator, viewPager, (tab, position) -> {}).attach();

        binding.btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < adapter.getItemCount() - 1) {
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                PreferenceManager.getInstance(this).setOnboardingCompleted(true);
                startActivity(new Intent(this, MainActivity.class));
                finish();
            }
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.btnNext.setText(position == adapter.getItemCount() - 1 ? "Commencer" : "Suivant");
            }
        });
    }

    private static class OnboardingAdapter extends RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder> {

        private final int[] titles = {
                R.string.onboarding_title_1,
                R.string.onboarding_title_2,
                R.string.onboarding_title_3,
                R.string.onboarding_title_4
        };
        private final int[] descriptions = {
                R.string.onboarding_desc_1,
                R.string.onboarding_desc_2,
                R.string.onboarding_desc_3,
                R.string.onboarding_desc_4
        };
        private final int[] icons = {
                R.drawable.ic_pill,
                R.drawable.ic_notifications,
                R.drawable.ic_stats,
                R.drawable.ic_sync
        };

        @NonNull
        @Override
        public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            OnboardingSlideBinding binding = OnboardingSlideBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new SlideViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
            holder.binding.ivIcon.setImageResource(icons[position]);
            holder.binding.tvTitle.setText(titles[position]);
            holder.binding.tvDescription.setText(descriptions[position]);
        }

        @Override
        public int getItemCount() { return titles.length; }

        static class SlideViewHolder extends RecyclerView.ViewHolder {
            final OnboardingSlideBinding binding;
            SlideViewHolder(OnboardingSlideBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}