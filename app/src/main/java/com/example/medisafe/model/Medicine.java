package com.example.medisafe.model;

import java.util.UUID;

public class Medicine {
    private String id;
    private String name;
    private String dosage;
    private int currentStock;
    private int initialStock;
    private String unit;
    private int lowStockThreshold;
    private Long expiryDate;
    private boolean reminderEnabled;
    private int reminderHour;
    private int colorIndex;
    private long createdAt;
    private Long lastTakenAt;
    private int reminderMinute;

    public Medicine() {}

    public Medicine(String name, String dosage, int initialStock, String unit) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.dosage = dosage;
        this.initialStock = initialStock;
        this.currentStock = initialStock;
        this.unit = unit;
        this.lowStockThreshold = 3;
        this.reminderEnabled = false;
        this.reminderHour = 8;
        this.createdAt = System.currentTimeMillis();
        this.reminderMinute = 0;
    }

    // Getters et Setters (générés automatiquement dans Android Studio)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDosage() { return dosage; }
    public void setDosage(String dosage) { this.dosage = dosage; }
    public int getCurrentStock() { return currentStock; }
    public void setCurrentStock(int currentStock) { this.currentStock = currentStock; }
    public int getInitialStock() { return initialStock; }
    public void setInitialStock(int initialStock) { this.initialStock = initialStock; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public int getLowStockThreshold() { return lowStockThreshold; }
    public void setLowStockThreshold(int lowStockThreshold) { this.lowStockThreshold = lowStockThreshold; }
    public Long getExpiryDate() { return expiryDate; }
    public void setExpiryDate(Long expiryDate) { this.expiryDate = expiryDate; }
    public boolean isReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }
    public int getReminderHour() { return reminderHour; }
    public void setReminderHour(int reminderHour) { this.reminderHour = reminderHour; }
    public int getColorIndex() { return colorIndex; }
    public void setColorIndex(int colorIndex) { this.colorIndex = colorIndex; }
    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
    public Long getLastTakenAt() { return lastTakenAt; }
    public void setLastTakenAt(Long lastTakenAt) { this.lastTakenAt = lastTakenAt; }

    public boolean isLowStock() {
        return currentStock <= lowStockThreshold;
    }

    public float getStockFraction() {
        if (initialStock == 0) return 0f;
        return (float) currentStock / initialStock;
    }
    public int getReminderMinute() { return reminderMinute; }
    public void setReminderMinute(int minute) { this.reminderMinute = minute; }
}