package com.good.boy.husky.city.finder;

import com.good.boy.husky.database.service.DatabaseService;
import java.sql.SQLException;

public class CityFinder {
    
    private static final DatabaseService databaseService = new DatabaseService();

    public static void main(String[] args) throws SQLException {
        System.out.println(databaseService.getListOfUnlocatedCities());
    }

}
