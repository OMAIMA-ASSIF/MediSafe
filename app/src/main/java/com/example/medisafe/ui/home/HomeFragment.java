package com.example.medisafe.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medisafe.R;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.example.medisafe.databinding.FragmentHomeBinding;
import com.example.medisafe.ui.add.AddMedicineFragment;
import com.example.medisafe.ui.adapter.MedicineAdapter;
import com.google.android.material.snackbar.Snackbar;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
    private MedicineAdapter adapter;

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
        adapter = new MedicineAdapter(new MedicineAdapter.OnMedicineListener() {
            @Override
            public void onTake(MedicineEntity medicine) {
                viewModel.takeMedicine(medicine.id);
                Snackbar.make(binding.getRoot(), "✔️ " + medicine.name + " : -1", Snackbar.LENGTH_SHORT).show();
            }

            @Override
            public void onClick(MedicineEntity medicine) {
                // Optionnel : ouvrir détail ou édition
            }

            @Override
            public void onLongClick(MedicineEntity medicine) {
                viewModel.deleteMedicine(medicine);
                Snackbar.make(binding.getRoot(), "🗑️ " + medicine.name + " supprimé", Snackbar.LENGTH_SHORT).show();
            }
        });
        binding.recyclerView.setAdapter(adapter);
    }

    private void observeMedicines() {
        viewModel.getAllMedicines().observe(getViewLifecycleOwner(), medicines -> {
            if (medicines == null || medicines.isEmpty()) {
                binding.recyclerView.setVisibility(View.GONE);
                binding.emptyState.setVisibility(View.VISIBLE);
            } else {
                binding.recyclerView.setVisibility(View.VISIBLE);
                binding.emptyState.setVisibility(View.GONE);
                adapter.submitList(medicines);
            }
        });
    }

    private void setupFab() {
        binding.fabAdd.setOnClickListener(v -> {
            new AddMedicineFragment().show(getParentFragmentManager(), "add_medicine");
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}