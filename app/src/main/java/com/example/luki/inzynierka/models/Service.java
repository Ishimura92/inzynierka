package com.example.luki.inzynierka.models;

import java.util.Date;

import io.realm.RealmObject;

public class Service extends RealmObject{

    private int id;
    private String serviceType;
    private float odometer;
    private float price;
    private String date;
    private Notification notification;

    public Service(){
    }

    public Service(int id, String serviceType, float odometer, float price, String date, Notification notification) {
        this.id = id;
        this.serviceType = serviceType;
        this.odometer = odometer;
        this.price = price;
        this.date = date;
        this.notification = notification;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }
}
