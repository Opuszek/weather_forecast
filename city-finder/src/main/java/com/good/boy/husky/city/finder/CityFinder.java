package com.good.boy.husky.city.finder;

import com.good.boy.husky.city.finder.service.GeolocalizeService;
import com.good.boy.husky.database.entity.CitySimple;
import com.good.boy.husky.database.service.DatabaseService;
import java.sql.SQLException;
import java.util.List;

public class CityFinder {
    
    private static final DatabaseService databaseService = new DatabaseService();
    private static final GeolocalizeService geolocalizeService = 
            new GeolocalizeService();

    public static void main(String[] args) throws SQLException {
        List<CitySimple> unlocatedCities = databaseService.getListOfUnlocatedCities();
        unlocatedCities.stream().forEach(geolocalizeService::geolocalize);
    }

}
