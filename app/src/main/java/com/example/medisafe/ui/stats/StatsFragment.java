package com.example.medisafe.ui.stats;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
    private List<MedicineEntity> currentMedicines = new ArrayList<>(); // pour stocker la liste actuelle

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentStatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(StatsViewModel.class);

        // Observer le nombre total de médicaments
        viewModel.getTotalMedicines().observe(getViewLifecycleOwner(), count -> {
            binding.tvTotalMedicines.setText(String.valueOf(count));
        });

        // Observer le nombre de médicaments en stock faible
        viewModel.getLowStockCount().observe(getViewLifecycleOwner(), count -> {
            binding.tvLowStock.setText(String.valueOf(count));
        });

        // Exemple pour le total pris (à remplacer par de vraies données)
        viewModel.getTotalTaken().observe(getViewLifecycleOwner(), taken -> {
            binding.tvTotalTaken.setText(String.valueOf(taken));
        });

        // Taux de conformité (exemple)
        viewModel.getComplianceRate().observe(getViewLifecycleOwner(), rate -> {
            binding.tvCompliance.setText(String.format("%.0f%%", rate));
        });

        // Pour le graphique (PieChart) – conserver le code existant
        viewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
            currentMedicines = medicines;
            if (medicines == null || medicines.isEmpty()) {
                binding.pieChart.setVisibility(View.GONE);
                binding.emptyStatsContainer.setVisibility(View.VISIBLE);
            } else {
                binding.pieChart.setVisibility(View.VISIBLE);
                binding.emptyStatsContainer.setVisibility(View.GONE);
                setupPieChart(medicines);
            }
        });

        // --- Ajout des listeners sur les cartes ---

        // Carte "Médicaments"
        binding.medicineCard.setOnClickListener(v -> {
            if (currentMedicines == null || currentMedicines.isEmpty()) {
                Toast.makeText(getContext(), "Aucun médicament", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuilder sb = new StringBuilder();
            for (MedicineEntity med : currentMedicines) {
                sb.append("• ").append(med.name)
                        .append(" (").append(med.currentStock).append("/").append(med.initialStock)
                        .append(" ").append(med.unit).append(")\n");
            }
            new AlertDialog.Builder(requireContext())
                    .setTitle("📋 Médicaments (" + currentMedicines.size() + ")")
                    .setMessage(sb.toString())
                    .setPositiveButton("OK", null)
                    .show();
        });

        // Carte "Stock faible"
        binding.lowStockCard.setOnClickListener(v -> {
            if (currentMedicines == null || currentMedicines.isEmpty()) {
                Toast.makeText(getContext(), "Aucun médicament", Toast.LENGTH_SHORT).show();
                return;
            }
            List<MedicineEntity> lowStockList = new ArrayList<>();
            for (MedicineEntity med : currentMedicines) {
                if (med.currentStock <= med.lowStockThreshold) {
                    lowStockList.add(med);
                }
            }
            if (lowStockList.isEmpty()) {
                Toast.makeText(getContext(), "✅ Aucun médicament en stock faible", Toast.LENGTH_SHORT).show();
            } else {
                StringBuilder sb = new StringBuilder();
                for (MedicineEntity med : lowStockList) {
                    sb.append("• ").append(med.name)
                            .append(" : ").append(med.currentStock).append(" ").append(med.unit).append("\n");
                }
                new AlertDialog.Builder(requireContext())
                        .setTitle("⚠️ Stock faible (" + lowStockList.size() + ")")
                        .setMessage(sb.toString())
                        .setPositiveButton("OK", null)
                        .show();
            }
        });

        // Carte "Pris" (historique des prises)
        binding.takenCard.setOnClickListener(v -> {
            if (currentMedicines == null || currentMedicines.isEmpty()) {
                Toast.makeText(getContext(), "Aucune donnée", Toast.LENGTH_SHORT).show();
                return;
            }
            int totalPris = 0;
            StringBuilder sb = new StringBuilder();
            for (MedicineEntity med : currentMedicines) {
                int taken = med.getTakenCount();
                totalPris += taken;
                if (taken > 0) {
                    sb.append("• ").append(med.name).append(" : ").append(taken).append(" prise(s)\n");
                }
            }
            if (totalPris == 0) {
                Toast.makeText(getContext(), "Aucune prise enregistrée", Toast.LENGTH_SHORT).show();
            } else {
                new AlertDialog.Builder(requireContext())
                        .setTitle("💊 Historique des prises (total : " + totalPris + ")")
                        .setMessage(sb.toString())
                        .setPositiveButton("OK", null)
                        .show();
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