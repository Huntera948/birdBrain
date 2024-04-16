package com.example.birdbrain.Database;

import android.app.Application;


import com.example.birdbrain.DAO.ExcursionDAO;
import com.example.birdbrain.DAO.BirdDAO;
import com.example.birdbrain.Entities.Excursion;
import com.example.birdbrain.Entities.Bird;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Repository {
    private ExcursionDAO mExcursionDAO;
    private BirdDAO mBirdDAO;
    private List<Bird> mAllBirds;
    private List<Excursion> mAllExcursions;

    private static int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public Repository(Application application) {
        BirdDatabaseBuilder db = BirdDatabaseBuilder.getDatabase(application);
        mExcursionDAO = db.excursionDAO();
        mBirdDAO = db.birdDAO();
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

    public List<Excursion> getAllExcursions() {
        databaseExecutor.execute(() -> {
            mAllExcursions = mExcursionDAO.getAllExcursions();
        });

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return mAllExcursions;
    }

    public void insert(Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.insert(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void update(Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.update(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void delete(Excursion excursion) {
        databaseExecutor.execute(() -> {
            mExcursionDAO.delete(excursion);
        });
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}