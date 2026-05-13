package com.example.medisafe.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.example.medisafe.data.repository.MedicineRepository;
import java.util.List;

public class HomeViewModel extends AndroidViewModel {

    private final MedicineRepository repository;
    private final LiveData<List<MedicineEntity>> allMedicines;

    public HomeViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);
        allMedicines = repository.getMedicines();
    }

    public LiveData<List<MedicineEntity>> getAllMedicines() {
        return allMedicines;
    }

    public void takeMedicine(String medicineId) {
        repository.takeMedicine(medicineId);
    }

    public void deleteMedicine(MedicineEntity medicine) {
        repository.deleteMedicine(medicine);
    }

    public void increaseStock(String medicineId, int amount) {
        repository.increaseStock(medicineId, amount);
    }
}