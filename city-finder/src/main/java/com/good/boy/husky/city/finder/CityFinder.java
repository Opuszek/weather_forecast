package com.good.boy.husky.city.finder;

import com.good.boy.husky.city.finder.service.GeolocalizeService;
import com.good.boy.husky.database.entity.CityError;
import com.good.boy.husky.database.entity.CityLocation;
import com.good.boy.husky.database.entity.CitySimple;
import com.good.boy.husky.database.service.DatabaseService;
import io.vavr.control.Either;
import java.sql.SQLException;
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
        for (var result : results) {
            if (result.isRight()) {
                databaseService.updateCityLocation(result.get());
            } else {
                databaseService.logCityLocationError(result.getLeft());
            }
        }
    }

}
