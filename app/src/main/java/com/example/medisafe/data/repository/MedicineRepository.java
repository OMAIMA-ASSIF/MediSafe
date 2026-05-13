package com.example.medisafe.data.repository;

import android.content.Context;
import androidx.lifecycle.LiveData;
import com.example.medisafe.data.local.MediSafeDatabase;
import com.example.medisafe.data.local.dao.MedicineDao;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.example.medisafe.model.Medicine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MedicineRepository {

    private final MedicineDao localDao;
    private final FirebaseFirestore firestore;
    private final String userId;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public MedicineRepository(Context context) {
        MediSafeDatabase db = MediSafeDatabase.getInstance(context);
        localDao = db.medicineDao();
        firestore = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        userId = user != null ? user.getUid() : "";
    }

    public LiveData<List<MedicineEntity>> getMedicines() {
        return localDao.getMedicines(userId);
    }

    public LiveData<List<MedicineEntity>> getLowStockMedicines() {
        return localDao.getLowStockMedicines(userId);
    }

    public LiveData<Integer> getLowStockCount() {
        return localDao.getLowStockCount(userId);
    }

    public void addMedicine(Medicine medicine) {
        MedicineEntity entity = toEntity(medicine);
        executor.execute(() -> {
            localDao.insertMedicine(entity);
            syncToFirestore(entity);
        });
    }

    public void takeMedicine(String medicineId) {
        executor.execute(() -> {
            localDao.takeMedicine(medicineId, System.currentTimeMillis());
            firestore.collection("users").document(userId)
                    .collection("medicines").document(medicineId)
                    .update("currentStock", FieldValue.increment(-1),
                            "lastTakenAt", System.currentTimeMillis());
        });
    }

    public void deleteMedicine(MedicineEntity medicine) {
        executor.execute(() -> {
            localDao.deleteMedicine(medicine);
            firestore.collection("users").document(userId)
                    .collection("medicines").document(medicine.id).delete();
        });
    }

    private void syncToFirestore(MedicineEntity entity) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", entity.id);
        data.put("userId", entity.userId);
        data.put("name", entity.name);
        data.put("dosage", entity.dosage);
        data.put("currentStock", entity.currentStock);
        data.put("initialStock", entity.initialStock);
        data.put("unit", entity.unit);
        data.put("lowStockThreshold", entity.lowStockThreshold);
        data.put("createdAt", entity.createdAt);
        data.put("takenCount", entity.takenCount);

        firestore.collection("users").document(userId)
                .collection("medicines").document(entity.id)
                .set(data, SetOptions.merge());
    }

    private MedicineEntity toEntity(Medicine m) {
        MedicineEntity e = new MedicineEntity();
        e.id = m.getId();
        e.userId = userId;
        e.name = m.getName();
        e.dosage = m.getDosage();
        e.currentStock = m.getCurrentStock();
        e.initialStock = m.getInitialStock();
        e.unit = m.getUnit();
        e.lowStockThreshold = m.getLowStockThreshold();
        e.reminderEnabled = m.isReminderEnabled();
        e.reminderHour = m.getReminderHour();
        e.colorIndex = m.getColorIndex();
        return e;
    }

    public LiveData<Integer> getTotalTaken() {
        return localDao.getTotalTakenCount(userId);
    }

}