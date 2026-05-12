package com.example.medisafe.ui.add;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import com.example.medisafe.data.repository.MedicineRepository;
import com.example.medisafe.model.Medicine;

public class AddMedicineViewModel extends AndroidViewModel {

    private final MedicineRepository repository;

    public AddMedicineViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);
    }

    public void addMedicine(String name, String dosage, int initialStock, String unit) {
        Medicine medicine = new Medicine(name, dosage, initialStock, unit);
        repository.addMedicine(medicine);
    }
}