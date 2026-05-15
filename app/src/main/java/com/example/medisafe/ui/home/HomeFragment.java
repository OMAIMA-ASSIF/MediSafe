package com.example.medisafe.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medisafe.R;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.example.medisafe.databinding.FragmentHomeBinding;
import com.example.medisafe.ui.add.AddMedicineFragment;
import com.example.medisafe.ui.adapter.MedicineAdapter;
import com.example.medisafe.utils.ReminderScheduler;
import com.google.android.material.snackbar.Snackbar;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.medisafe.worker.LowStockWorker;
import androidx.recyclerview.widget.ConcatAdapter;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private MedicineAdapter adapter;
    private MedicineAdapter reminderAdapter;
    private MedicineAdapter normalAdapter;
    private ConcatAdapter concatAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        setupRecyclerView();
        observeMedicines();
        setupFab();
    }

    private void setupRecyclerView() {
        // Le même listener pour les deux adaptateurs
        MedicineAdapter.OnMedicineListener listener = new MedicineAdapter.OnMedicineListener() {
            @Override
            public void onTake(MedicineEntity medicine) {
                viewModel.takeMedicine(medicine.id);
                Snackbar.make(binding.getRoot(), "✔️ " + medicine.name + " : -1", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onAdd(MedicineEntity medicine) {
                viewModel.increaseStock(medicine.id, 1);
                Snackbar.make(binding.getRoot(), "➕ " + medicine.name + " +1", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onClick(MedicineEntity medicine) {
                // Optionnel : ouvrir détail ou édition
            }

            @Override
            public void onLongClick(MedicineEntity medicine) {
                new AlertDialog.Builder(requireContext())
                        .setTitle("Supprimer")
                        .setMessage("Voulez-vous vraiment supprimer " + medicine.name + " ?")
                        .setPositiveButton("Supprimer", (dialog, which) -> {
                            ReminderScheduler.cancelReminder(requireContext(), medicine.id);
                            viewModel.deleteMedicine(medicine);
                            Snackbar.make(binding.getRoot(), "🗑️ " + medicine.name + " supprimé", Snackbar.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("Annuler", null)
                        .show();
            }
        };

        reminderAdapter = new MedicineAdapter(listener);
        normalAdapter = new MedicineAdapter(listener);
        concatAdapter = new ConcatAdapter(reminderAdapter, normalAdapter);
        binding.recyclerView.setAdapter(concatAdapter);
    }
    private void observeMedicines() {
        viewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
            if (medicines == null || medicines.isEmpty()) {
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
                return;
            }
            binding.recyclerView.setVisibility(View.VISIBLE);
            binding.emptyState.setVisibility(View.GONE);

            // Séparer les médicaments
            List<MedicineEntity> reminderList = new ArrayList<>();
            List<MedicineEntity> normalList = new ArrayList<>();
            for (MedicineEntity med : medicines) {
                if (med.reminderEnabled) {
                    reminderList.add(med);
                } else {
                    normalList.add(med);
                }
            }
            reminderAdapter.submitList(reminderList);
            normalAdapter.submitList(normalList);
        });
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            new AddMedicineFragment().show(getParentFragmentManager(), "add_medicine");
        });

        // Long-clic pour forcer la vérification des notifications
        binding.fabAdd.setOnLongClickListener(v -> {
            triggerLowStockCheck();
            return true; // indique que l'événement est consommé
        });
    }

    private void triggerLowStockCheck() {
        OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(LowStockWorker.class).build();
        WorkManager.getInstance(requireContext()).enqueue(workRequest);
        Snackbar.make(binding.getRoot(), "🔍 Vérification des stocks lancée", Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}