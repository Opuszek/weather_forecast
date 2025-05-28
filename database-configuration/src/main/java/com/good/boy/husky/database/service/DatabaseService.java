package com.good.boy.husky.database.service;

import com.good.boy.husky.database.configuration.DatabaseConfiguration;
import com.good.boy.husky.database.entity.CityError;
import com.good.boy.husky.database.entity.CityLocation;
import com.good.boy.husky.database.entity.CitySimple;
import com.good.boy.husky.database.entity.ForecastError;
import com.good.boy.husky.database.entity.WeatherForecast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseService {

    public DatabaseService() {
        DatabaseConfiguration.validateConfiguration();
    }

    public List<CitySimple> getListOfUnlocatedCities() throws SQLException {
        try (Connection con = getConnection()) {
            return getListOfCities(con, false, false);
        }
    }

    public List<CitySimple> getListOfLocatedCities() throws SQLException {
        try (Connection con = getConnection()) {
            return getListOfCities(con, true, false);
        }
    }

    public List<CityLocation> getListOfCityLocations() throws SQLException {
        try (Connection con = getConnection()) {
            return getListOfCityLocations(con);
        }
    }

    public void updateCityLocation(CityLocation location) throws SQLException {
        try (Connection con = getConnection()) {
            updateCityLocation(location, con);
        }
    }

    public void addWeatherForecast(WeatherForecast wf) throws SQLException {
        try (Connection con = getConnection()) {
            addWeatherForecast(wf, con);
        }
    }
    
        public void addForecastError(ForecastError fe) throws SQLException {
        try (Connection con = getConnection()) {
            addForecastError(fe, con);
        }
    }

    public void logCityLocationError(CityError error) throws SQLException {
        try (Connection con = getConnection()) {
            logCityLocationError(error, con);
        }
    }

    private List<CitySimple> getListOfCities(Connection con,
            boolean located, boolean invalid)
            throws SQLException {
        List<CitySimple> cities = new ArrayList<>();
        try (Statement stmt = con.createStatement()) {
            String selectSql = String.format("SELECT id,name,country "
                    + "FROM city "
                    + "where located=%d and invalid=%d", located ? 1 : 0,
                    invalid ? 1 : 0);
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

    private List<CityLocation> getListOfCityLocations(Connection con) throws SQLException {
        List<CityLocation> locations = new ArrayList<>();
        try (Statement stmt = con.createStatement()) {
            String selectSql = "SELECT id,longitude,latitude "
                    + "FROM city "
                    + "where located=1 and invalid=0";
            try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
                while (resultSet.next()) {
                    CityLocation city = new CityLocation.CityLocationBuilder()
                            .id(resultSet.getInt("id"))
                            .longitude(resultSet.getFloat("longitude"))
                            .latitude(resultSet.getFloat("latitude")).build();
                    locations.add(city);
                }
            }
        }
        return locations;
    }

    public void updateCityLocation(CityLocation loc, Connection con) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            String updateSql = String.format("UPDATE city "
                    + "SET longitude=%f, latitude=%f, error=NULL, located=1 "
                    + "where id=%d", loc.getLongitude(), loc.getLatitude(), loc.getId());
            stmt.executeUpdate(updateSql);
        }
    }

    public void addWeatherForecast(WeatherForecast wf, Connection con) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            String insertSql = String.format("INSERT INTO temperature_forecast "
                    + "(city_id, max_temperature, min_temperature, rain_sum, sunrise, sunset, day)\n "
                    + "values (%d, %f, %f, %f, FROM_UNIXTIME(%d), FROM_UNIXTIME(%d), FROM_UNIXTIME(%d))",
                    wf.getCityId(), wf.getMaxTemp(), wf.getMinTemp(), wf.getRainSum(),
                    wf.getSunrise(), wf.getSunset(), wf.getDay());
            stmt.executeUpdate(insertSql);
        }

    }
    
     public void addForecastError(ForecastError fe, Connection con) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            String insertSql = String.format("INSERT INTO forecast_error "
                    + "(city_id, date, error)\n "
                    + "values (%d, FROM_UNIXTIME(%d), \"%s\")",
                    fe.getCityId(), fe.getUnixTime(), fe.getError());
            stmt.executeUpdate(insertSql);
        }

    }

    public void logCityLocationError(CityError err, Connection con) throws SQLException {
        try (Statement stmt = con.createStatement()) {
            String selectSql = String.format("UPDATE city "
                    + "SET error=\"%s\", invalid=%B, number_of_tries=number_of_tries+1 "
                    + "where id=%d", err.getError(), err.isInvalid(), err.getId());
            stmt.executeUpdate(selectSql);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager
                .getConnection(DatabaseConfiguration.getUrl(),
                        DatabaseConfiguration.getUser(),
                        DatabaseConfiguration.getPassword());
    }

}
