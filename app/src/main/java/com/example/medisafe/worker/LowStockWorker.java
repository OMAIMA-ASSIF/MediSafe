package com.example.medisafe.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import com.example.medisafe.MainActivity;
import com.example.medisafe.R;
import com.example.medisafe.data.local.MediSafeDatabase;
import com.example.medisafe.data.local.entity.MedicineEntity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.List;
import java.util.concurrent.TimeUnit;
import androidx.core.content.ContextCompat;
import androidx.annotation.NonNull;
public class LowStockWorker extends Worker {

    public LowStockWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) return Result.success();

        MediSafeDatabase db = MediSafeDatabase.getInstance(getApplicationContext());
        List<MedicineEntity> lowStockMeds = db.medicineDao().getLowStockMedicinesSync(user.getUid());

        for (MedicineEntity med : lowStockMeds) {
            sendNotification(med);
        }
        return Result.success();
    }

    private void sendNotification(MedicineEntity medicine) {
        Context ctx = getApplicationContext();
        NotificationManager manager = (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        // Canal pour Android 8+
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "low_stock_channel",
                    "Stock bas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription("Alerte quand le stock est faible");
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(ctx, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                ctx, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, "low_stock_channel")
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("⚠️ Stock bas : " + medicine.name)
                .setContentText("Plus que " + medicine.currentStock + " " + medicine.unit + " — Pense à racheter !")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(ctx, R.color.medi_warning))
                .setContentIntent(pendingIntent);

        manager.notify(medicine.id.hashCode(), builder.build());
    }

    public static void schedule(Context context) {
        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
                LowStockWorker.class, 12, TimeUnit.HOURS)
                .build();
        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "low_stock_check",
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
        );
    }
}