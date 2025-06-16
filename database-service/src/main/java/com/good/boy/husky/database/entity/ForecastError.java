package com.good.boy.husky.database.entity;

public class ForecastError {
    private int cityId;
    private long unixTime;
    private String error;

    public int getCityId() {
        return cityId;
    }

    public ForecastError setCityId(int cityId) {
        this.cityId = cityId;
        return this;
    }

    public long getUnixTime() {
        return unixTime;
    }

    public ForecastError setUnixTime(long unixTime) {
        this.unixTime = unixTime;
        return this;
    }

    public String getError() {
        return error;
    }

    public ForecastError setError(String error) {
        this.error = error;
        return this;
    }

    @Override
    public String toString() {
        return "ForecastError{" + "cityId=" + cityId + ", date=" + unixTime + ", error=" + error + '}';
    }
    
}
