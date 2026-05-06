package com.example.medisafe.data.local;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.example.medisafe.data.local.dao.MedicineDao;
import com.example.medisafe.data.local.entity.MedicineEntity;

@Database(entities = {MedicineEntity.class}, version = 1, exportSchema = false)
public abstract class MediSafeDatabase extends RoomDatabase {

    private static volatile MediSafeDatabase INSTANCE;

    public abstract MedicineDao medicineDao();

    public static MediSafeDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (MediSafeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MediSafeDatabase.class,
                            "medisafe_db"
                    ).fallbackToDestructiveMigration().build();
                }
            }
        }
        return INSTANCE;
    }
}