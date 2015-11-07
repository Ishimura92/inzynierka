package com.example.luki.inzynierka.Models;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;

public class Vehicle extends RealmObject {

    private int id;
    private String brand;
    private String model;
    private Date productionDate;
    private String color;
    private String engineType;
    private float engineCapacity;
    private float odometer;
    private String bodyType;
    private RealmList<Refueling> refuelings;
    private RealmList<Repair> repairs;
    private RealmList<Service> services;

    public Vehicle() {
    }

    public Vehicle(int id, String brand, String model, Date productionDate, String color, String engineType, float engineCapacity, float odometer, String bodyType, RealmList<Refueling> refuelings, RealmList<Repair> repairs, RealmList<Service> services) {
        this.id = id;
        this.brand = brand;
        this.model = model;
        this.productionDate = productionDate;
        this.color = color;
        this.engineType = engineType;
        this.engineCapacity = engineCapacity;
        this.odometer = odometer;
        this.bodyType = bodyType;
        this.refuelings = refuelings;
        this.repairs = repairs;
        this.services = services;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Date getProductionDate() {
        return productionDate;
    }

    public void setProductionDate(Date productionDate) {
        this.productionDate = productionDate;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getEngineType() {
        return engineType;
    }

    public void setEngineType(String engineType) {
        this.engineType = engineType;
    }

    public float getEngineCapacity() {
        return engineCapacity;
    }

    public void setEngineCapacity(float engineCapacity) {
        this.engineCapacity = engineCapacity;
    }

    public float getOdometer() {
        return odometer;
    }

    public void setOdometer(float odometer) {
        this.odometer = odometer;
    }

    public String getBodyType() {
        return bodyType;
    }

    public void setBodyType(String bodyType) {
        this.bodyType = bodyType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public RealmList<Refueling> getRefuelings() {
        return refuelings;
    }

    public void setRefuelings(RealmList<Refueling> refuelings) {
        this.refuelings = refuelings;
    }

    public RealmList<Repair> getRepairs() {
        return repairs;
    }

    public void setRepairs(RealmList<Repair> repairs) {
        this.repairs = repairs;
    }

    public RealmList<Service> getServices() {
        return services;
    }

    public void setServices(RealmList<Service> services) {
        this.services = services;
    }
}
