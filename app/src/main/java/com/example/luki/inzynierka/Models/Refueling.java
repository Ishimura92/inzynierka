package com.example.luki.inzynierka.Models;

import java.sql.Ref;
import java.util.Date;

import io.realm.RealmObject;

public class Refueling extends RealmObject{

    private float liters;
    private float price;
    private int odometer;
    private Date date;

    public Refueling(){
    }

    public Refueling(float liters, float price, int odometer, Date date) {
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
        this.date = date;
    }

    public float getLiters() {
        return liters;
    }

    public void setLiters(float liters) {
        this.liters = liters;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public int getOdometer() {
        return odometer;
    }

    public void setOdometer(int odometer) {
        this.odometer = odometer;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
