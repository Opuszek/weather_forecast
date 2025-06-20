package com.jklis.city.finder;

import com.jklis.city.finder.service.GeolocalizeService;
import com.jklis.database.entity.CityError;
import com.jklis.database.entity.CityLocation;
import com.jklis.database.entity.CitySimple;
import com.jklis.database.service.DatabaseService;
import io.vavr.control.Either;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CityFinder {

    private static final DatabaseService databaseService = new DatabaseService();
    private static final GeolocalizeService geolocalizeService
            = new GeolocalizeService();

    private static final Logger LOGGER = Logger.getLogger(CityFinder.class.getName());

    public static void main(String[] args) throws SQLException {
        List<CitySimple> unlocatedCities = databaseService.getListOfUnlocatedCities();
        List<Either<CityError, CityLocation>> results = unlocatedCities.stream()
                .map(geolocalizeService::geolocalize)
                .collect(Collectors.toList());
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
