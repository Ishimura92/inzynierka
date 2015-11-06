package com.example.luki.inzynierka.Models;

import java.util.Date;

import io.realm.RealmObject;

public class Service extends RealmObject{

    private String serviceType;
    private float odometer;
    private float price;
    private Date date;

    public Service(){
    }

    public Service(String serviceType, float odometer, float price, Date date) {
        this.serviceType = serviceType;
        this.odometer = odometer;
        this.price = price;
        this.date = date;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public float getOdometer() {
        return odometer;
    }

    public void setOdometer(float odometer) {
        this.odometer = odometer;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }
}
