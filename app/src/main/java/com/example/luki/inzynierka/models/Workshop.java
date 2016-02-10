package com.example.luki.inzynierka.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Workshop extends RealmObject{

    @PrimaryKey
    private int id;

    private String name;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;

    public Workshop(){
    }

    public Workshop(int id, String name, String firstName, String lastName, String phoneNumber, String address) {
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
