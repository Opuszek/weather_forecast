/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.good.boy.husky.database.service;

import com.good.boy.husky.database.configuration.DatabaseConfiguration;
import com.good.boy.husky.database.entity.CitySimple;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author opuszek
 */
public class DatabaseService {
    
    public DatabaseService() {
        DatabaseConfiguration.validateConfiguration();
    }

    public List<CitySimple> getListOfUnlocatedCities() throws SQLException {
        try (Connection con = getConnection()) {
            return getListOfUnlocatedCities(con);
        }
    }

    private List<CitySimple> getListOfUnlocatedCities(Connection con)
            throws SQLException {
        List<CitySimple> cities = new ArrayList<>();
        try (Statement stmt = con.createStatement()) {
            String selectSql = "SELECT id,name,country "
                    + "FROM city "
                    + "where locatedOrInvalid=0";
            try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
                while (resultSet.next()) {
                    CitySimple city = new CitySimple();
                    city.setId(resultSet.getInt("id"));
                    city.setName(resultSet.getString("name"));
                    city.setCountry(resultSet.getString("country"));
                    cities.add(city);
                }
            }
        }
        return cities;
    }

    private Connection getConnection() throws SQLException {
        return DriverManager
                .getConnection(DatabaseConfiguration.getUrl(),
                        DatabaseConfiguration.getUser(),
                        DatabaseConfiguration.getPassword());
    }

}
