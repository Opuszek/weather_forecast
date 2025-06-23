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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WeatherFinder {

    private static final DatabaseService databaseService = new DatabaseService();
    private static final WeatherService weatherService = new WeatherService();
    private static final Logger LOGGER = Logger.getLogger(WeatherFinder.class.getName());

    public static void main(String[] args) throws SQLException, URISyntaxException,
            IOException, InterruptedException {
        List<Either<ForecastError, WeatherForecast>> results
                = databaseService.getListOfCityLocations().stream()
                        .map(weatherService::getWeatherForecast)
                        .collect(Collectors.toList());
        databaseService.addWeatherForecasts(getRightEithers(results));
        databaseService.addForecastErrors(getLeftEithers(results));
        logResults(results);
    }

    private static void logResults(Collection<Either<ForecastError, WeatherForecast>> results) {
        Collection<ForecastError> errs = getLeftEithers(results);
        String logSuccess = String.format("Operation finished with %d succesful forecast(s)",
                getRightEithers(results).size());
        String logErrors = errs.isEmpty() ? ""
                : String.format(" and %d following errors:%s%s", errs.size(),
                        System.lineSeparator(), listErrors(errs));
        LOGGER.log(Level.INFO, logSuccess + logErrors);
    }

    private static String listErrors(Collection<ForecastError> locs) {
        return locs.stream()
                .collect(Collectors.toMap(ForecastError::getError, v -> 1L, Long::sum))
                .entrySet().stream().map(
                        e -> String.format("Exception %s occured %d times",
                                e.getKey(), e.getValue())
                ).collect(Collectors.joining("," + System.lineSeparator()));
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
