package com.good.boy.husky.database.entity;

public class CityError {

    private int id;
    private String error;
    private boolean locatedOrInvalid;

    public CityError(int id, String error, boolean located) {
        this.id = id;
        this.error = error;
        this.locatedOrInvalid = located;
    }
    
    

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

    public boolean isLocatedOrInvalid() {
        return locatedOrInvalid;
    }

    public void setLocatedOrInvalid(boolean locatedOrInvalid) {
        this.locatedOrInvalid = locatedOrInvalid;
    }

    @Override
    public String toString() {
        return "CityError{" + "id=" + id + ", error=" + error + 
                ", locatedOrInvalid=" + locatedOrInvalid + '}';
    }
    
    

    

}
