package com.example.medisafe.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import com.example.medisafe.R;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.example.medisafe.databinding.ItemMedicineBinding;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<MedicineEntity> medicines = new ArrayList<>();
    private final OnMedicineListener listener;

    public interface OnMedicineListener {
        void onTake(MedicineEntity medicine);
        void onAdd(MedicineEntity medicine);
        void onClick(MedicineEntity medicine);
        void onLongClick(MedicineEntity medicine);
    }

    public MedicineAdapter(OnMedicineListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMedicineBinding binding = ItemMedicineBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new MedicineViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        holder.bind(medicines.get(position));
    }

    @Override
    public int getItemCount() { return medicines.size(); }

    public void submitList(List<MedicineEntity> newList) {
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override public int getOldListSize() { return medicines.size(); }
            @Override public int getNewListSize() { return newList.size(); }
            @Override public boolean areItemsTheSame(int o, int n) {
                return medicines.get(o).id.equals(newList.get(n).id);
            }
            @Override public boolean areContentsTheSame(int o, int n) {
                return medicines.get(o).currentStock == newList.get(n).currentStock
                        && medicines.get(o).name.equals(newList.get(n).name);
            }
        });
        medicines = new ArrayList<>(newList);
        result.dispatchUpdatesTo(this);
    }
    private String getTimeUntilReminder(int reminderHour, int reminderMinute) {
        Calendar now = Calendar.getInstance();
        Calendar next = Calendar.getInstance();
        next.set(Calendar.HOUR_OF_DAY, reminderHour);
        next.set(Calendar.MINUTE, reminderMinute);
        next.set(Calendar.SECOND, 0);
        if (next.getTimeInMillis() <= now.getTimeInMillis()) {
            next.add(Calendar.DAY_OF_YEAR, 1);
        }
        long diff = next.getTimeInMillis() - now.getTimeInMillis();
        long hours = diff / (60 * 60 * 1000);
        long minutes = (diff % (60 * 60 * 1000)) / (60 * 1000);
        if (hours == 0) return "Rappel dans " + minutes + " min";
        else return "Rappel dans " + hours + "h" + minutes + "min";
    }
    class MedicineViewHolder extends RecyclerView.ViewHolder {
        private final ItemMedicineBinding b;

        MedicineViewHolder(ItemMedicineBinding binding) {
            super(binding.getRoot());
            b = binding;
        }

        void bind(MedicineEntity m) {
            b.tvName.setText(m.name);
            b.tvDosage.setText(m.dosage);
            b.tvStock.setText(m.currentStock + " / " + m.initialStock + " " + m.unit);

            float fraction = m.initialStock > 0 ? (float) m.currentStock / m.initialStock : 0f;
            b.progressStock.setProgress((int) (fraction * 100));

            int progressColor;
            if (fraction <= 0.25f) progressColor = R.color.medi_danger;
            else if (fraction <= 0.5f) progressColor = R.color.medi_warning;
            else progressColor = R.color.medi_green_400;
            b.progressStock.setProgressBarColor(
                    ContextCompat.getColor(b.getRoot().getContext(), progressColor));

            boolean isLow = m.currentStock <= m.lowStockThreshold;
            b.cardMedicine.setStrokeColor(ContextCompat.getColor(b.getRoot().getContext(),
                    isLow ? R.color.medi_danger : R.color.medi_gray_100));
            b.cardMedicine.setStrokeWidth(isLow ? 2 : 1);
            if (isLow) {
                b.tvStock.setTextColor(ContextCompat.getColor(
                        b.getRoot().getContext(), R.color.medi_danger));
                b.ivWarning.setVisibility(View.VISIBLE);
            } else {
                b.ivWarning.setVisibility(View.GONE);
            }

            // Affichage du rappel
            if (m.reminderEnabled) {
                String timeLeft = getTimeUntilReminder(m.reminderHour, m.reminderMinute);
                b.tvReminder.setVisibility(View.VISIBLE);
                b.tvReminder.setText(timeLeft);
            } else {
                b.tvReminder.setVisibility(View.GONE);
            }


            b.btnTake.setOnClickListener(v -> listener.onTake(m));
            b.btnAdd.setOnClickListener(v -> {
                if (listener != null) listener.onAdd(m);
            });
            b.cardMedicine.setOnClickListener(v -> listener.onClick(m));
            b.cardMedicine.setOnLongClickListener(v -> {
                listener.onLongClick(m);
                return true;
            });

        }
    }
}