package com.example.medisafe.ui.add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import com.example.medisafe.databinding.FragmentAddMedicineBinding;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.snackbar.Snackbar;

public class AddMedicineFragment extends BottomSheetDialogFragment {

    private FragmentAddMedicineBinding binding;
    private AddMedicineViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddMedicineBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(AddMedicineViewModel.class);

        binding.btnSave.setOnClickListener(v -> {
            String name = binding.etName.getText().toString().trim();
            String dosage = binding.etDosage.getText().toString().trim();
            String unit = binding.etUnit.getText().toString().trim();
            String stockStr = binding.etInitialStock.getText().toString().trim();

            if (name.isEmpty() || dosage.isEmpty() || stockStr.isEmpty()) {
                Snackbar.make(binding.getRoot(), "Remplis tous les champs", Snackbar.LENGTH_SHORT).show();
                return;
            }
            int initialStock = Integer.parseInt(stockStr);
            viewModel.addMedicine(name, dosage, initialStock, unit);
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}