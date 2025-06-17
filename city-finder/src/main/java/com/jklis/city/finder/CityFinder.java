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
import java.util.stream.Collectors;

public class CityFinder {

    private static final DatabaseService databaseService = new DatabaseService();
    private static final GeolocalizeService geolocalizeService
            = new GeolocalizeService();

    public static void main(String[] args) throws SQLException {
        List<CitySimple> unlocatedCities = databaseService.getListOfUnlocatedCities();
        List<Either<CityError, CityLocation>> results = unlocatedCities.stream()
                .map(geolocalizeService::geolocalize)
                .collect(Collectors.toList());
        databaseService.updateCityLocation(getRightEithers(results));
        databaseService.logCityLocationError(getLeftEithers(results));
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
