package com.example.birdbrain.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "birds")
public class Bird {
    @PrimaryKey(autoGenerate = true)
    private int birdID;
    private String birdName;
    private String birdNotes;
    private String birdSightingDate;
    private String birdLocationDescription;

    public Bird(int birdID, String birdName, String birdNotes, String birdSightingDate, String birdLocationDescription) {
        this.birdID = birdID;
        this.birdName = birdName;
        this.birdNotes = birdNotes;
        this.birdSightingDate = birdSightingDate;
        this.birdLocationDescription = birdLocationDescription;
    }

    public int getBirdID() {
        return birdID;
    }

    public void setBirdID(int birdID) {
        this.birdID = birdID;
    }

    public String getBirdName() {
        return birdName;
    }

    public void setBirdName(String birdName) {
        this.birdName = birdName;
    }

    public String getBirdNotes() {
        return birdNotes;
    }

    public void setBirdNotes(String birdNotes) {
        this.birdNotes = birdNotes;
    }

    public String getBirdSightingDate() {
        return birdSightingDate;
    }

    public void setBirdSightingDate(String birdSightingDate) {
        this.birdSightingDate = birdSightingDate;
    }

    public String getBirdLocationDescription() {
        return birdLocationDescription;
    }

    public void setBirdLocationDescription(String birdLocationDescription) {
        this.birdLocationDescription = birdLocationDescription;
    }

}

