package com.example.medisafe.data.local.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.example.medisafe.data.local.entity.MedicineEntity;
import java.util.List;

@Dao
public interface MedicineDao {

    @Query("SELECT * FROM medicines WHERE userId = :userId ORDER BY name ASC")
    LiveData<List<MedicineEntity>> getMedicines(String userId);

    @Query("SELECT * FROM medicines WHERE userId = :userId AND currentStock <= lowStockThreshold")
    LiveData<List<MedicineEntity>> getLowStockMedicines(String userId);

    // Version synchrone pour le Worker
    @Query("SELECT * FROM medicines WHERE userId = :userId AND currentStock <= lowStockThreshold")
    List<MedicineEntity> getLowStockMedicinesSync(String userId);

    @Query("SELECT * FROM medicines WHERE userId = :userId AND name LIKE '%' || :query || '%'")
    LiveData<List<MedicineEntity>> searchMedicines(String userId, String query);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMedicine(MedicineEntity medicine);

    @Update
    void updateMedicine(MedicineEntity medicine);

    @Delete
    void deleteMedicine(MedicineEntity medicine);

    @Query("UPDATE medicines SET currentStock = currentStock - 1, lastTakenAt = :timestamp, takenCount = takenCount + 1 WHERE id = :id AND currentStock > 0")
    void takeMedicine(String id, long timestamp);

    @Query("SELECT COUNT(*) FROM medicines WHERE userId = :userId AND currentStock <= lowStockThreshold")
    LiveData<Integer> getLowStockCount(String userId);

    @Query("SELECT SUM(takenCount) FROM medicines WHERE userId = :userId")
    LiveData<Integer> getTotalTakenCount(String userId);


}