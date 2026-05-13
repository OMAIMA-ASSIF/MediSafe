package com.example.medisafe.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.core.app.NotificationCompat;
import com.example.medisafe.MainActivity;
import com.example.medisafe.R;

public class NotificationHelper {

    // Pour les rappels horaires (AlarmManager)
    public static void sendReminderNotification(Context context, String medicineName, String medicineId) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    "reminder_channel",
                    "Rappels médicaments",
                    NotificationManager.IMPORTANCE_HIGH
            );
            manager.createNotificationChannel(channel);
        }

        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("medicine_id", medicineId);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                medicineId.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "reminder_channel")
                .setSmallIcon(R.drawable.ic_pill)
                .setContentTitle("Rappel médicament")
                .setContentText("Il est temps de prendre " + medicineName)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        manager.notify(medicineId.hashCode(), builder.build());
    }

    // Tu pourras ajouter d'autres méthodes ici plus tard (ex: stock bas)
}