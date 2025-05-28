package com.good.boy.husky.weather.finder;

import com.good.boy.husky.database.entity.CityLocation;
import com.good.boy.husky.database.entity.WeatherForecast;
import com.good.boy.husky.database.service.DatabaseService;
import com.good.boy.husky.weather.finder.service.WeatherService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;


public class WeatherFinder {

    private static final DatabaseService databaseService = new DatabaseService();
    private static final WeatherService weatherService = new WeatherService();

    public static void main(String[] args) throws SQLException, URISyntaxException, 
            IOException, InterruptedException {
        List<CityLocation> locatedCities = databaseService.getListOfCityLocations();
        for (CityLocation loc : locatedCities) {
        WeatherForecast wf = weatherService.getWeatherDetails(loc);
        databaseService.addWeatherForecast(wf);
        }
    }
}
