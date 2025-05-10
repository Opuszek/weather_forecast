/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.good.boy.husky.database.entity;

/**
 *
 * @author opuszek
 */
public class CityLocation {

    private int id;
    private float latitude;
    private float longitude;

    public CityLocation(int id, float latitude, float longitude) {
        this.id = id;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "CityLocation{" + "id=" + id + ", latitude=" + latitude
                + ", longitude=" + longitude + '}';
    }

    public static class CityLocationBuilder {

        private int id;
        private float latitude;
        private float longitude;

        public CityLocationBuilder id(int id) {
            this.id = id;
            return this;
        }

        public CityLocationBuilder latitude(float latitude) {
            this.latitude = latitude;
            return this;
        }

        public CityLocationBuilder longitude(float longitude) {
            this.longitude = longitude;
            return this;
        }

        public CityLocation build() {
            return new CityLocation(id, latitude, longitude);
        }

    }

}
