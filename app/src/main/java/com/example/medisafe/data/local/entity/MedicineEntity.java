package com.example.medisafe.data.local.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import java.util.UUID;
import androidx.annotation.NonNull;

@Entity(tableName = "medicines")
public class MedicineEntity {
    @PrimaryKey
    @NonNull
    public String id;
    public String userId;
    public String name;
    public String dosage;
    public int currentStock;
    public int initialStock;
    public String unit;
    public int lowStockThreshold;
    public Long expiryDate;
    public boolean reminderEnabled;
    public int reminderHour;
    public int colorIndex;
    public long createdAt;
    public Long lastTakenAt;
    public int takenCount;

    public MedicineEntity() {
        this.id = UUID.randomUUID().toString();
        this.unit = "tablets";
        this.lowStockThreshold = 3;
        this.reminderHour = 8;
        this.createdAt = System.currentTimeMillis();
        takenCount = 0;
    }

    public int getTakenCount() {
        return this.takenCount;
    }
}