package com.jklis.database.entity;

public class CitySimple {
    private int id;
    private String name;
    private String country;

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

    @Override
    public String toString() {
        return "CitySimple{" + "id=" + id + ", name=" 
                + name + ", country=" + country + '}';
    }

    
}