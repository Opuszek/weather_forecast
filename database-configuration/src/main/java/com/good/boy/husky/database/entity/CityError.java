package com.good.boy.husky.database.entity;

public class CityError {

    private int id;
    private String error;
    private int numberOfTries;
    private boolean located;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public boolean isLocated() {
        return located;
    }

    public void setLocated(boolean located) {
        this.located = located;
    }

    @Override
    public String toString() {
        return "CityError{" + "id=" + id + ", error=" + error 
                + ", numberOfTries=" + numberOfTries + ", located=" 
                + located + '}';
    }

    

}
