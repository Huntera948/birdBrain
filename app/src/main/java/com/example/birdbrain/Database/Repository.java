package com.example.birdbrain.Database;

import android.app.Application;
import android.util.Log;


import com.example.birdbrain.DAO.BirdDAO;
import com.example.birdbrain.DAO.LogDAO;
import com.example.birdbrain.Entities.Bird;
import com.example.birdbrain.Entities.LogEntry;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Repository {

    private BirdDAO mBirdDAO;
    private LogDAO mLogDAO;
    private List<Bird> mAllBirds;
    private List<LogEntry> mAllLogs;

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
        databaseExecutor.execute(() -> mLogDAO.insert(log));
    }

    public List<LogEntry> getAllLogs() {
        databaseExecutor.execute(() -> {
            mAllLogs = mLogDAO.getAllLogs();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mAllLogs;
    }

    public Bird getBirdById(int birdID) {
        Callable<Bird> callable = new Callable<Bird>() {
            @Override
            public Bird call() throws Exception {
                return mBirdDAO.getBirdById(birdID);
            }
        };

        Future<Bird> future = databaseExecutor.submit(callable);
        try {
            // This will block the thread until the callable completes and result is available
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            Log.e("Repository", "Error getting bird by ID", e);
            return null;
        }
    }
}
