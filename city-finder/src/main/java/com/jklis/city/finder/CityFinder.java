package com.jklis.city.finder;

import com.jklis.city.finder.service.GeolocalizeService;
import com.jklis.database.entity.CityError;
import com.jklis.database.entity.CityLocation;
import com.jklis.database.entity.CitySimple;
import com.jklis.database.service.DatabaseService;
import com.jklis.database.utilities.Utilities;
import com.jklis.utilities.Either;
import static com.jklis.utilities.Utilities.functionToCallable;
import static com.jklis.utilities.Utilities.getLeftEithers;
import static com.jklis.utilities.Utilities.getRightEithers;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CityFinder {

    private static final DatabaseService databaseService = new DatabaseService();
    private static final GeolocalizeService geolocalizeService
            = new GeolocalizeService();
    private static final int THREADS_NUMBER = 
            Integer.parseInt(Utilities.getProperty("threadNumber"));

    private static final Logger LOGGER = Logger.getLogger(CityFinder.class.getName());

    public static void main(String[] args) throws SQLException {
        List<Either<CityError, CityLocation>> results = new ArrayList<>();
        final ExecutorService executor
                = Executors.newFixedThreadPool(THREADS_NUMBER);
        List<CitySimple> unlocatedCities = databaseService.getListOfUnlocatedCities();
        
        List<Future<Either<CityError, CityLocation>>> futures = unlocatedCities.stream()
                .map(city -> functionToCallable(geolocalizeService::geolocalize, city))
                .map(executor::submit)
                .collect(Collectors.toList());
        for (var future : futures) {
            try {
                results.add(future.get());
            } catch (InterruptedException | ExecutionException ex) {
                LOGGER.log(Level.SEVERE, null, ex);
            }
        }
        executor.shutdown();
        
        databaseService.updateCityLocations(getRightEithers(results));
        databaseService.logCityLocationErrors(getLeftEithers(results));
        logResults(results);
    }

    private static void logResults(Collection<Either<CityError, CityLocation>> results) {
        Collection<CityError> errs = getLeftEithers(results);
        String logSuccess = String.format("Operation finished with %d succesful location(s)",
                getRightEithers(results).size());
        String logErrors = errs.isEmpty() ? ""
                : String.format(" and %d following errors:%s%s", errs.size(),
                        System.lineSeparator(), listErrors(errs));
        LOGGER.log(Level.INFO, logSuccess + logErrors);
    }

    private static String listErrors(Collection<CityError> locs) {
        return locs.stream()
                .collect(Collectors.toMap(CityError::getError, v -> 1L, Long::sum))
                .entrySet().stream().map(
                        e -> String.format("Exception %s occured %d times",
                                e.getKey(), e.getValue())
                ).collect(Collectors.joining("," + System.lineSeparator()));
    }

}
