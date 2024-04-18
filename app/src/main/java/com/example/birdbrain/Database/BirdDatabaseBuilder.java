package com.example.birdbrain.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.birdbrain.DAO.BirdDAO;
import com.example.birdbrain.DAO.LogDAO;
import com.example.birdbrain.Entities.Bird;
import com.example.birdbrain.Entities.LogEntry;

@Database(entities = {Bird.class, LogEntry.class}, version = 7, exportSchema = false)
public abstract class BirdDatabaseBuilder extends RoomDatabase {
    public abstract BirdDAO birdDAO();
    public abstract LogDAO logDAO();

    private static volatile BirdDatabaseBuilder INSTANCE;

    static BirdDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BirdDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BirdDatabaseBuilder.class, "MyBirdDatabase.db")
                            //.addMigrations(MIGRATION_1_2)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            database.execSQL("CREATE TABLE IF NOT EXISTS `activity_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `dateTime` TEXT, `userId` TEXT, `action` TEXT, `details` TEXT)");
        }
    };

}
