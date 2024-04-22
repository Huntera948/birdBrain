package com.example.birdbrain.Entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "users")
public class User {
    @PrimaryKey
    @NonNull
    public String username;

    @ColumnInfo(name = "hashed_password")
    public String hashedPassword;

    @ColumnInfo(name = "salt")
    public byte[] salt;  // Store salt only if it varies per user
}

