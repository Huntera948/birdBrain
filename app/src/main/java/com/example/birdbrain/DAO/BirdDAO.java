package com.example.birdbrain.DAO;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.birdbrain.Entities.Bird;

import java.util.List;

@Dao
public interface BirdDAO {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Bird bird);

    @Update
    void update(Bird bird);

    @Delete
    void delete(Bird bird);

    @Query("SELECT * FROM BIRDS ORDER BY BIRDID ASC")
    List<Bird> getAllBirds();

    @Query("SELECT * FROM birds WHERE birdID = :birdID")
    Bird getBirdById(int birdID);
}
