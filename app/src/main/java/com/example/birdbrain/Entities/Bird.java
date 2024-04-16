package com.example.birdbrain.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "birds")
public class Bird {
    @PrimaryKey(autoGenerate = true)
    private int birdID;
    private String birdName;
    private String birdHotel;
    private String birdStartDate;
    private String birdEndDate;

    public Bird(int birdID, String birdName, String birdHotel, String birdStartDate, String birdEndDate) {
        this.birdID = birdID;
        this.birdName = birdName;
        this.birdHotel = birdHotel;
        this.birdStartDate = birdStartDate;
        this.birdEndDate = birdEndDate;
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

    public String getBirdHotel() {
        return birdHotel;
    }

    public void setBirdHotel(String birdHotel) {
        this.birdHotel = birdHotel;
    }

    public String getBirdStartDate() {
        return birdStartDate;
    }

    public void setBirdStartDate(String birdStartDate) {
        this.birdStartDate = birdStartDate;
    }

    public String getBirdEndDate() {
        return birdEndDate;
    }

    public void setBirdEndDate(String birdEndDate) {
        this.birdEndDate = birdEndDate;
    }

}

