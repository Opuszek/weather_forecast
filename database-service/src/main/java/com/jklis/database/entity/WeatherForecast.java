package com.jklis.database.entity;

public class WeatherForecast {

    private int id;
    private int cityId;
    private float maxTemp;
    private float minTemp;
    private float rainSum;
    private long sunrise;
    private long sunset;
    private long day;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }

    public float getMaxTemp() {
        return maxTemp;
    }

    public void setMaxTemp(float maxTemp) {
        this.maxTemp = maxTemp;
    }

    public float getMinTemp() {
        return minTemp;
    }

    public void setMinTemp(float minTemp) {
        this.minTemp = minTemp;
    }

    public float getRainSum() {
        return rainSum;
    }

    public void setRainSum(float rainSum) {
        this.rainSum = rainSum;
    }

    public Long getSunrise() {
        return sunrise;
    }

    public void setSunrise(long sunrise) {
        this.sunrise = sunrise;
    }

    public Long getSunset() {
        return sunset;
    }

    public void setSunset(long sunset) {
        this.sunset = sunset;
    }

    public Long getDay() {
        return day;
    }

    public void setDay(long day) {
        this.day = day;
    }

    @Override
    public String toString() {
        return "WeatherForecast{" + "id=" + id + ", cityId=" + cityId
                + ", maxTemp=" + maxTemp + ", minTemp=" + minTemp + ", rainSum="
                + rainSum + ", sunrise=" + sunrise + ", sunset=" + sunset + ", day="
                + day + '}';
    }

    public static class WeatherForecastBuilder {

        private int id;
        private int cityId;
        private float maxTemp;
        private float minTemp;
        private float rainSum;
        private long sunrise;
        private long sunset;
        private long day;

        public WeatherForecastBuilder id(int id) {
            this.id = id;
            return this;
        }

        public WeatherForecastBuilder cityId(int cityId) {
            this.cityId = cityId;
            return this;
        }

        public WeatherForecastBuilder maxTemp(float maxTemp) {
            this.maxTemp = maxTemp;
            return this;
        }

        public WeatherForecastBuilder minTemp(float minTemp) {
            this.minTemp = minTemp;
            return this;
        }

        public WeatherForecastBuilder rainSum(float rainSum) {
            this.rainSum = rainSum;
            return this;
        }

        public WeatherForecastBuilder sunrise(long sunrise) {
            this.sunrise = sunrise;
            return this;
        }

        public WeatherForecastBuilder sunset(long sunset) {
            this.sunset = sunset;
            return this;
        }

        public WeatherForecastBuilder day(long day) {
            this.day = day;
            return this;
        }

        public WeatherForecast build() {
            WeatherForecast wf = new WeatherForecast();
            wf.setId(id);
            wf.setCityId(cityId);
            wf.setMaxTemp(maxTemp);
            wf.setMinTemp(minTemp);
            wf.setRainSum(rainSum);
            wf.setSunrise(sunrise);
            wf.setSunset(sunset);
            wf.setDay(day);
            return wf;
        }

    }

}
