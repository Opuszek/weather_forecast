/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.good.boy.husky.database.entity;

/**
 *
 * @author opuszek
 */
public class City {
    private int id;
    private String name;
    private String country;
    private float longitude;
    private float latitude;
    private String error;
    private int numberOfTries;
    private boolean repetable;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public int getNumberOfTries() {
        return numberOfTries;
    }

    public void setNumberOfTries(int numberOfTries) {
        this.numberOfTries = numberOfTries;
    }

    public boolean isRepetable() {
        return repetable;
    }

    public void setRepetable(boolean repetable) {
        this.repetable = repetable;
    }

    @Override
    public String toString() {
        return "City{" + "name=" + name + ", country=" + country + ", "
                + "longitude=" + longitude + ", latitude=" + latitude + ", "
                + "error=" + error + ", numberOfTries=" + numberOfTries + ", "
                + "repetable=" + repetable + '}';
    }
    
    
    
    
}