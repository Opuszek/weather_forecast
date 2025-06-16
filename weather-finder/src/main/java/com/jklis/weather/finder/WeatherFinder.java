package com.jklis.weather.finder;

import com.jklis.database.entity.CityLocation;
import com.jklis.database.entity.ForecastError;
import com.jklis.database.entity.WeatherForecast;
import com.jklis.database.service.DatabaseService;
import com.jklis.weather.finder.service.WeatherService;
import io.vavr.control.Either;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;


public class WeatherFinder {

    private static final DatabaseService databaseService = new DatabaseService();
    private static final WeatherService weatherService = new WeatherService();

    public static void main(String[] args) throws SQLException, URISyntaxException, 
            IOException, InterruptedException {
        List<Either<ForecastError,WeatherForecast>> results = 
                databaseService.getListOfCityLocations().stream()
                .map(weatherService::getWeatherDetails)
                .collect(Collectors.toList());
        for (var result : results) {
            if (result.isRight()) {
                databaseService.addWeatherForecast(result.get());
            } else {
                databaseService.addForecastError(result.getLeft());
            }
        }
    }
}
