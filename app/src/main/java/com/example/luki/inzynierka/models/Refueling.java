package com.example.luki.inzynierka.models;

import java.util.Date;

import io.realm.RealmObject;

public class Refueling extends RealmObject{

    private int id;
    private float liters;
    private float price;
    private int odometer;
    private String date;
    private String type;

    public Refueling(){
    }

    public Refueling(int id, float liters, float price, int odometer, String date, String type) {
        this.id = id;
        this.liters = liters;
        this.price = price;
        this.odometer = odometer;
        this.date = date;
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
