package com.example.luki.inzynierka.models;

import io.realm.RealmObject;

public class Notification extends RealmObject {

    private int id;
    private String name;
    private boolean isDateNotification;
    private String date;
    private float kilometers;

    public Notification() {

    }

    public Notification(int id, String name, boolean isDateNotification, String date, float kilometers) {
        this.id = id;
        this.name = name;
        this.isDateNotification = isDateNotification;
        this.date = date;
        this.kilometers = kilometers;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isDateNotification() {
        return isDateNotification;
    }

    public void setIsDateNotification(boolean isDateNotification) {
        this.isDateNotification = isDateNotification;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public float getKilometers() {
        return kilometers;
    }

    public void setKilometers(float kilometers) {
        this.kilometers = kilometers;
    }
}
