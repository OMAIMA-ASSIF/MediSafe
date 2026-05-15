package com.example.medisafe.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import com.example.medisafe.R;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.example.medisafe.databinding.FragmentHomeBinding;
import com.example.medisafe.ui.add.AddMedicineFragment;
import com.example.medisafe.ui.adapter.MedicineAdapter;
import com.example.medisafe.utils.ReminderScheduler;
import com.example.medisafe.worker.LowStockWorker;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel viewModel;
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
        // Listener commun pour les deux listes
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
                // Action optionnelle (édition, détail)
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

        // Use your existing drawables (e.g., ic_notifications, ic_pill, ic_reminder, etc.)
        TitleAdapter reminderTitle = new TitleAdapter("Médicaments avec rappel", R.drawable.ic_notifications);
        TitleAdapter normalTitle = new TitleAdapter("Autres médicaments", R.drawable.ic_drugs);

        // Combinaison : titre rappel → liste rappel → titre normal → liste normale
        concatAdapter = new ConcatAdapter(reminderTitle, reminderAdapter, normalTitle, normalAdapter);

        binding.recyclerView.setAdapter(concatAdapter);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
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

            // Séparation
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
        binding.fabAdd.setOnClickListener(v -> new AddMedicineFragment().show(getParentFragmentManager(), "add_medicine"));
        binding.fabAdd.setOnLongClickListener(v -> {
            triggerLowStockCheck();
            return true;
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

    // Adapter simple pour afficher un titre
    private static class TitleAdapter extends RecyclerView.Adapter<TitleAdapter.TitleViewHolder> {
        private final String title;
        private final int iconRes;  // drawable resource ID

        TitleAdapter(String title, int iconRes) {
            this.title = title;
            this.iconRes = iconRes;
        }

        @NonNull
        @Override
        public TitleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView textView = new TextView(parent.getContext());
            textView.setText(title);
            textView.setTextSize(18);
            textView.setTypeface(android.graphics.Typeface.DEFAULT_BOLD);
            textView.setPadding(16, 32, 16, 8);
            textView.setTextColor(parent.getContext().getColor(R.color.medi_primary_800));

            // Set compound drawable (icon left of text)
            android.graphics.drawable.Drawable icon = parent.getContext().getDrawable(iconRes);
            if (icon != null) {
                icon.setBounds(0, 0, icon.getIntrinsicWidth(), icon.getIntrinsicHeight());
                textView.setCompoundDrawables(icon, null, null, null);
                textView.setCompoundDrawablePadding(16);
            }
            return new TitleViewHolder(textView);
        }

        @Override
        public void onBindViewHolder(@NonNull TitleViewHolder holder, int position) { }

        @Override
        public int getItemCount() {
            return 1;
        }

        static class TitleViewHolder extends RecyclerView.ViewHolder {
            TitleViewHolder(@NonNull TextView itemView) {
                super(itemView);
            }
        }
    }
}