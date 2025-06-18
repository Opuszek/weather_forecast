package com.jklis.weather.finder;

import com.jklis.database.entity.ForecastError;
import com.jklis.database.entity.WeatherForecast;
import com.jklis.database.service.DatabaseService;
import com.jklis.weather.finder.service.WeatherService;
import io.vavr.control.Either;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class WeatherFinder {

    private static final DatabaseService databaseService = new DatabaseService();
    private static final WeatherService weatherService = new WeatherService();

    public static void main(String[] args) throws SQLException, URISyntaxException,
            IOException, InterruptedException {
        List<Either<ForecastError, WeatherForecast>> results
                = databaseService.getListOfCityLocations().stream()
                        .map(weatherService::getWeatherForecast)
                        .collect(Collectors.toList());
        databaseService.addWeatherForecasts(getRightEithers(results));
        databaseService.addForecastErrors(getLeftEithers(results));
    }

    private static <T, Y> List<T> getLeftEithers(Collection<Either<T, Y>> eithers) {
        return eithers.stream()
                .filter(Either::isLeft)
                .map(Either::getLeft)
                .collect(Collectors.toList());
    }

    private static <T, Y> List<Y> getRightEithers(Collection<Either<T, Y>> eithers) {
        return eithers.stream()
                .filter(Either::isRight)
                .map(Either::get)
                .collect(Collectors.toList());
    }
}
