package com.example.medisafe.ui.add;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.medisafe.data.repository.MedicineRepository;
import com.example.medisafe.model.Medicine;
import com.example.medisafe.utils.ReminderScheduler;

public class AddMedicineViewModel extends AndroidViewModel {

    private final MedicineRepository repository;
    private final MutableLiveData<Boolean> medicineAdded = new MutableLiveData<>();

    public AddMedicineViewModel(@NonNull Application application) {
        super(application);
        repository = new MedicineRepository(application);
    }

    public LiveData<Boolean> getMedicineAdded() {
        return medicineAdded;
    }

    public void addMedicine(String name, String dosage, int initialStock, String unit,
                            boolean reminderEnabled, int reminderHour, int reminderMinute) {
        Medicine medicine = new Medicine(name, dosage, initialStock, unit);
        medicine.setReminderEnabled(reminderEnabled);
        medicine.setReminderHour(reminderHour);
        medicine.setReminderMinute(reminderMinute);  // stocker les minutes séparément

        repository.addMedicine(medicine, () -> {
            medicineAdded.postValue(true);
            if (reminderEnabled) {
                ReminderScheduler.scheduleReminder(getApplication(),
                        medicine.getId(),
                        medicine.getName(),
                        reminderHour,
                        reminderMinute);
            }
        });
    }
}