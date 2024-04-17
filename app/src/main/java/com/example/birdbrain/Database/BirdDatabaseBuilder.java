package com.example.birdbrain.Database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.birdbrain.DAO.BirdDAO;
import com.example.birdbrain.Entities.Bird;

@Database(entities = {Bird.class}, version = 2, exportSchema = false)
public abstract class BirdDatabaseBuilder extends RoomDatabase {
    public abstract BirdDAO birdDAO();

    private static volatile BirdDatabaseBuilder INSTANCE;

    static BirdDatabaseBuilder getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (BirdDatabaseBuilder.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), BirdDatabaseBuilder.class, "MyBirdDatabase.db")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
