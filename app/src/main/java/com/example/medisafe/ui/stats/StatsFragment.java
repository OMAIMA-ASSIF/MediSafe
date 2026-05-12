package com.example.medisafe.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.example.medisafe.R;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.example.medisafe.databinding.FragmentStatsBinding;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import java.util.ArrayList;
import java.util.List;

public class StatsFragment extends Fragment {

    private FragmentStatsBinding binding;
    private StatsViewModel viewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StatsViewModel.class);

        viewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
            if (medicines == null || medicines.isEmpty()) {
                binding.pieChart.setVisibility(View.GONE);
                binding.tvEmptyStats.setVisibility(View.VISIBLE);
            } else {
                binding.pieChart.setVisibility(View.VISIBLE);
                binding.tvEmptyStats.setVisibility(View.GONE);
                setupPieChart(medicines);
            }
        });
    }

    private void setupPieChart(List<MedicineEntity> medicines) {
        List<PieEntry> entries = new ArrayList<>();
        List<Integer> colors = new ArrayList<>();

        for (MedicineEntity med : medicines) {
            String label = med.name + " (" + med.currentStock + "/" + med.initialStock + ")";
            entries.add(new PieEntry(med.currentStock, label));
            colors.add(getColorForStock(med));
        }

        PieDataSet dataSet = new PieDataSet(entries, "Stock restant");
        dataSet.setColors(colors);
        dataSet.setValueTextSize(12f);
        dataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(dataSet);
        binding.pieChart.setData(pieData);
        binding.pieChart.setCenterText("Stock actuel");
        binding.pieChart.setCenterTextSize(14f);
        binding.pieChart.setDescription(null);
        binding.pieChart.invalidate();
    }

    private int getColorForStock(MedicineEntity med) {
        float fraction = (float) med.currentStock / med.initialStock;
        if (fraction <= 0.25f) return getResources().getColor(R.color.medi_danger);
        if (fraction <= 0.5f) return getResources().getColor(R.color.medi_warning);
        return getResources().getColor(R.color.medi_green_400);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}