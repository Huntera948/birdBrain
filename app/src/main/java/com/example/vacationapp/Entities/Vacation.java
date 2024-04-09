package com.example.vacationapp.Entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "vacations")
public class Vacation {
    @PrimaryKey(autoGenerate = true)
    private int vacationID;

    private String vacationName;
    private double vacationPrice;

    public Vacation(int vacationID, String vacationName, double vacationPrice) {
        this.vacationID = vacationID;
        this.vacationName = vacationName;
        this.vacationPrice = vacationPrice;
    }

    public int getVacationID() {
        return vacationID;
    }

    public void setVacationID(int vacationID) {
        this.vacationID = vacationID;
    }

    public String getVacationName() {
        return vacationName;
    }

    public void setVacationName(String vacationName) {
        this.vacationName = vacationName;
    }

    public double getVacationPrice() {
        return vacationPrice;
    }

    public void setVacationPrice(double vacationPrice) {
        this.vacationPrice = vacationPrice;
    }
}

