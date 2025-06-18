package com.jklis.database.service;

import com.jklis.database.configuration.DatabaseConfiguration;
import com.jklis.database.entity.CityError;
import com.jklis.database.entity.CityLocation;
import com.jklis.database.entity.CitySimple;
import com.jklis.database.entity.ForecastError;
import com.jklis.database.entity.WeatherForecast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class DatabaseService {

    private final String username;
    private final String password;
    private final String url;

    public DatabaseService(String url, String username, String password) {
        this.username = username;
        this.password = password;
        this.url = url;
    }

    public DatabaseService() {
        DatabaseConfiguration.validateConfiguration();
        this.url = DatabaseConfiguration.getUrl();
        this.username = DatabaseConfiguration.getUser();
        this.password = DatabaseConfiguration.getPassword();

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

    public void updateCityLocations(Collection<CityLocation> locs) throws SQLException {
        executeBatchUpdate(locs, this::getUpdateCityLocationStmt);
    }

    public void addWeatherForecasts(Collection<WeatherForecast> wfs) throws SQLException {
        executeBatchUpdate(wfs, this::createAddWeatherForecastStatement);
    }

    public void addForecastErrors(Collection<ForecastError> fes) throws SQLException {
        executeBatchUpdate(fes, this::createAddForecastErrorStatement);
    }

    public void logCityLocationErrors(Collection<CityError> errs) throws SQLException {
        executeBatchUpdate(errs, this::getUpdateCityLocationErrorStmt);
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

    private String getUpdateCityLocationStmt(CityLocation loc) {
        return String.format("UPDATE city "
                + "SET longitude=%f, latitude=%f, error=NULL, located=1 "
                + "where id=%d", loc.getLongitude(), loc.getLatitude(), loc.getId());
    }
    
    private String createAddWeatherForecastStatement(WeatherForecast wf) {
        return String.format("INSERT INTO temperature_forecast "
                + "(city_id, max_temperature, min_temperature, rain_sum, sunrise, sunset, day)\n "
                + "values (%d, %f, %f, %f, FROM_UNIXTIME(%d), FROM_UNIXTIME(%d), FROM_UNIXTIME(%d))",
                wf.getCityId(), wf.getMaxTemp(), wf.getMinTemp(), wf.getRainSum(),
                wf.getSunrise(), wf.getSunset(), wf.getDay());
    }
    
    private String createAddForecastErrorStatement(ForecastError fe) {
        return String.format("INSERT INTO forecast_error "
                + "(city_id, date, error)\n "
                + "values (%d, FROM_UNIXTIME(%d), \"%s\")",
                fe.getCityId(), fe.getUnixTime(), fe.getError());
    }

    private String getUpdateCityLocationErrorStmt(CityError err) {
        return String.format("UPDATE city "
                + "SET error='%s', invalid=%B, number_of_tries=number_of_tries+1 "
                + "where id=%d", err.getError(), err.isInvalid(), err.getId());
    }

    private Connection getConnection() throws SQLException {
        return DriverManager
                .getConnection(this.url, this.username, this.password);
    }
    
    private <T> void executeBatchUpdate(Collection<T> entities, Function<T, String> createStmt) throws SQLException {
        try (Connection con = getConnection()) {
            try (Statement stmt = con.createStatement()) {
                List<String> queries = entities.stream().map(createStmt)
                        .collect(Collectors.toList());
                for (String query : queries) {
                    stmt.addBatch(query);
                }
                stmt.executeBatch();
            }
        }
    }

}
