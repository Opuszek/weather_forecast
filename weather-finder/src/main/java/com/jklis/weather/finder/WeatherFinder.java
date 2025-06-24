package com.jklis.weather.finder;

import com.jklis.database.entity.ForecastError;
import com.jklis.database.entity.WeatherForecast;
import com.jklis.database.service.DatabaseService;
import com.jklis.database.utilities.Utilities;
import com.jklis.utilities.Either;
import static com.jklis.utilities.Utilities.functionToCallable;
import static com.jklis.utilities.Utilities.getLeftEithers;
import static com.jklis.utilities.Utilities.getRightEithers;
import com.jklis.weather.finder.service.WeatherService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class WeatherFinder {

    private static final DatabaseService databaseService = new DatabaseService();
    private static final WeatherService weatherService = new WeatherService();
    private static final Logger LOGGER = Logger.getLogger(WeatherFinder.class.getName());

    public static void main(String[] args) throws SQLException, URISyntaxException,
            IOException, InterruptedException {
        List<Either<ForecastError, WeatherForecast>> results = new ArrayList<>();
        final ScheduledExecutorService executor
                = Executors.newSingleThreadScheduledExecutor();
        
        Queue<Callable<Either<ForecastError, WeatherForecast>>> callables
                = databaseService.getListOfCityLocations().stream()
                        .map(loc -> functionToCallable(weatherService::getWeatherForecast, loc))
                        .collect(Collectors.toCollection(LinkedList::new));

        executor.scheduleWithFixedDelay(() -> {
            try {
                if (callables.isEmpty()) {
                    executor.shutdown();
                    return;
                }
                Either<ForecastError, WeatherForecast> result = callables.poll().call();

                results.add(result);
            } catch (Exception ex) {
                Logger.getLogger(WeatherFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }, 0, getTaskDelay(), TimeUnit.MILLISECONDS);
        executor.awaitTermination(Long.MAX_VALUE, TimeUnit.SECONDS);
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
    
    private static int getTaskDelay() {
        return Integer.parseInt(Utilities.getProperty("task.delay").trim());
    }

}
