package com.example.medisafe.ui.add;

import android.os.Bundle;
import android.text.TextUtils;
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

        binding.switchReminder.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.tilReminderTime.setVisibility(isChecked ? View.VISIBLE : View.GONE);
        });

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

            boolean reminderEnabled = binding.switchReminder.isChecked();
            int reminderHour = 0;
            int reminderMinute = 0;
            if (reminderEnabled) {
                String timeStr = binding.etReminderTime.getText().toString().trim();
                if (TextUtils.isEmpty(timeStr)) {
                    Snackbar.make(binding.getRoot(), "Indique une heure de rappel", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                String[] parts = timeStr.split(":");
                if (parts.length != 2) {
                    Snackbar.make(binding.getRoot(), "Format d'heure invalide (ex: 08:30)", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                try {
                    reminderHour = Integer.parseInt(parts[0]);
                    reminderMinute = Integer.parseInt(parts[1]);
                } catch (NumberFormatException e) {
                    Snackbar.make(binding.getRoot(), "Heure invalide", Snackbar.LENGTH_SHORT).show();
                    return;
                }
                if (reminderHour < 0 || reminderHour > 23 || reminderMinute < 0 || reminderMinute > 59) {
                    Snackbar.make(binding.getRoot(), "Heure hors limites", Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }

            viewModel.addMedicine(name, dosage, initialStock, unit, reminderEnabled, reminderHour, reminderMinute);
            dismiss();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}