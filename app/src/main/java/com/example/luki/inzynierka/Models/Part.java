package com.example.luki.inzynierka.Models;

import io.realm.RealmObject;

public class Part extends RealmObject {

    private String name;
    private float price;
    private String brand;

    public Part(){
    }

    public Part(String name, float price, String brand) {
        this.name = name;
        this.price = price;
        this.brand = brand;
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
}
