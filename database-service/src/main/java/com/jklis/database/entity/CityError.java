package com.jklis.database.entity;

public class CityError {

    private int id;
    private String error;
    private boolean invalid;

    public CityError(int id, String error, boolean invalid) {
        this.id = id;
        this.error = error;
        this.invalid = invalid;
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

    public boolean isInvalid() {
        return invalid;
    }

    public void setInvalid(boolean invalid) {
        this.invalid = invalid;
    }

    @Override
    public String toString() {
        return "CityError{" + "id=" + id + ", error=" + error + 
                ", invalid=" + invalid + '}';
    }
    
    

    

}
