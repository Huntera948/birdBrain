package com.example.birdbrain.Database;

import android.app.Application;


import com.example.birdbrain.DAO.BirdDAO;
import com.example.birdbrain.DAO.LogDAO;
import com.example.birdbrain.Entities.Bird;
import com.example.birdbrain.Entities.LogEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {

    private BirdDAO mBirdDAO;
    private LogDAO mLogDAO;
    private List<Bird> mAllBirds;

    private static int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        BirdDatabaseBuilder db = BirdDatabaseBuilder.getDatabase(application);
        mBirdDAO = db.birdDAO();
        mLogDAO = db.logDAO();
    }

    public List<Bird> getAllBirds() {
        databaseExecutor.execute(() -> {
            mAllBirds = mBirdDAO.getAllBirds();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mAllBirds;
    }

    public void insert(Bird bird) {
        databaseExecutor.execute(() -> {
            mBirdDAO.insert(bird);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Bird bird) {
        databaseExecutor.execute(() -> {
            mBirdDAO.update(bird);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(Bird bird) {
        databaseExecutor.execute(() -> {
            mBirdDAO.delete(bird);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void insertLog(LogEntry log) {
        databaseExecutor.execute(() -> {
            mLogDAO.insert(log);
        });
    }
    public void insertLog(String userId, String action, String details) {
        LogEntry log = new LogEntry();
        log.setDateTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        log.setUserId(userId);
        log.setAction(action);
        log.setDetails(details);
        mLogDAO.insert(log);
    }
}
