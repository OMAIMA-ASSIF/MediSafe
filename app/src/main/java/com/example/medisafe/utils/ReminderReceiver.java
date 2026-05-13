package com.example.medisafe.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.example.medisafe.MainActivity;

public class ReminderReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicineName = intent.getStringExtra("medicine_name");
        String medicineId = intent.getStringExtra("medicine_id");
        NotificationHelper.sendReminderNotification(context, medicineName, medicineId);
    }
}