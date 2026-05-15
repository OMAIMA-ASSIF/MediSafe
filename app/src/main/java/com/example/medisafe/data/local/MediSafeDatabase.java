package com.example.medisafe.data.local;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.medisafe.data.local.dao.MedicineDao;
import com.example.medisafe.data.local.entity.MedicineEntity;

@Database(entities = {MedicineEntity.class}, version = 3, exportSchema = false)
public abstract class MediSafeDatabase extends RoomDatabase {

    private static volatile MediSafeDatabase INSTANCE;

    public abstract MedicineDao medicineDao();

    // On supprime les migrations car on utilise fallbackToDestructiveMigration
    // (cela évite les erreurs de colonne en double)

    public static MediSafeDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MediSafeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    MediSafeDatabase.class,
                                    "medisafe_db"
                            )
                            .fallbackToDestructiveMigration() // recrée la base propre
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}