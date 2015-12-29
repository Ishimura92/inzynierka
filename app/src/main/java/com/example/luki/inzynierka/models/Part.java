package com.example.luki.inzynierka.models;

import io.realm.RealmObject;

public class Part extends RealmObject {

    private int id;
    private String name;
    private float price;
    private String brand;
    private String photoPath;

    public Part(){
    }

    public Part(int id, String name, float price, String brand) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.photoPath = null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }
}
