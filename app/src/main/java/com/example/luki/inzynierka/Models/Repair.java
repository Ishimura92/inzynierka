package com.example.luki.inzynierka.Models;

import java.util.Date;
import java.util.List;

import io.realm.RealmList;
import io.realm.RealmObject;

public class Repair extends RealmObject {

    private String title;
    private String description;
    private float totalCost;
    private float odometer;
    private RealmList<Part> parts;
    private Date date;
    private Workshop workshop;

    public Repair() {
    }

    public Repair(String title, String description, float totalCost, float odometer, RealmList<Part> parts, Date date, Workshop workshop) {
        this.title = title;
        this.description = description;
        this.totalCost = totalCost;
        this.odometer = odometer;
        this.parts = parts;
        this.date = date;
        this.workshop = workshop;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(float totalCost) {
        this.totalCost = totalCost;
    }

    public float getOdometer() {
        return odometer;
    }

    public void setOdometer(float odometer) {
        this.odometer = odometer;
    }

    public RealmList<Part> getParts() {
        return parts;
    }

    public void setParts(RealmList<Part> parts) {
        this.parts = parts;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Workshop getWorkshop() {
        return workshop;
    }

    public void setWorkshop(Workshop workshop) {
        this.workshop = workshop;
    }
}
