package com.example.birdbrain.DAO;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.birdbrain.Entities.LogEntry;

import java.util.List;

@Dao
public interface LogDAO {
    @Insert
    void insert(LogEntry log);

    @Query("SELECT * FROM LOGS")
    List<LogEntry> getAllLogs();
}

