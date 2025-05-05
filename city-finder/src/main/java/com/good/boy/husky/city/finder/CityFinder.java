package com.good.boy.husky.city.finder;

import com.good.boy.husky.database.configuration.DatabaseConfiguration;
import com.good.boy.husky.database.entity.City;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CityFinder {

    public static void main(String[] args) throws SQLException {
        System.out.println(DatabaseConfiguration.getUser());
        System.out.println(DatabaseConfiguration.getPassword());
        System.out.println(DatabaseConfiguration.getUrl());
        try (Connection con = DriverManager
                .getConnection(DatabaseConfiguration.getUrl(),
                        DatabaseConfiguration.getUser(),
                        DatabaseConfiguration.getPassword())) {
            List<City> cities = new ArrayList<>();
            try (Statement stmt = con.createStatement()) {
                String selectSql = "SELECT * FROM city";
                try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
                    while (resultSet.next()) {
                        City city = new City();
                        city.setId(resultSet.getInt("id"));
                        city.setName(resultSet.getString("name"));
                        city.setCountry(resultSet.getString("country"));
                        city.setLongitude(resultSet.getFloat("longitude"));
                        city.setLatitude(resultSet.getFloat("latitude"));
                        city.setError(resultSet.getString("error"));
                        city.setNumberOfTries(resultSet.getInt("number_of_tries"));
                        city.setRepetable(resultSet.getBoolean("repetable"));
                        cities.add(city);
                    }
                }
            }
            System.out.println("How many cities:" + cities.size());
            cities.stream().forEach(c -> System.out.println(c));
        }
    }

}
