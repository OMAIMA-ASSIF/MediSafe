package com.example.medisafe.ui.stats;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.example.medisafe.data.repository.MedicineRepository;
import java.util.List;

public class StatsViewModel extends AndroidViewModel {

    private final MedicineRepository repository;
    private final LiveData<List<MedicineEntity>> allMedicines;
    private final LiveData<Integer> totalMedicines;
    private final LiveData<Integer> totalTaken;
    private final LiveData<Float> complianceRate;
    private final LiveData<Integer> lowStockCount;

    public StatsViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);
        allMedicines = repository.getMedicines();

        totalMedicines = Transformations.map(allMedicines, list -> list == null ? 0 : list.size());
        lowStockCount = repository.getLowStockCount();

        totalTaken = repository.getTotalTaken();

        complianceRate = Transformations.map(allMedicines, list -> {
            if (list == null || list.isEmpty()) return 0f;
            int takenCount = 0;
            for (MedicineEntity med : list) {
                if (med.getTakenCount() > 0) takenCount++;
            }
            return (takenCount * 100f) / list.size();
        });
    }

    public LiveData<Integer> getTotalMedicines() { return totalMedicines; }
    public LiveData<Integer> getLowStockCount() { return lowStockCount; }
    public LiveData<Integer> getTotalTaken() { return totalTaken; }
    public LiveData<Float> getComplianceRate() { return complianceRate; }
    public LiveData<List<MedicineEntity>> getAllMedicines() { return allMedicines; }
}