package com.jklis.database.service;

import com.jklis.database.configuration.DatabaseConfiguration;
import com.jklis.database.entity.CityError;
import com.jklis.database.entity.CityLocation;
import com.jklis.database.entity.CitySimple;
import com.jklis.database.entity.ForecastError;
import com.jklis.database.entity.WeatherForecast;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

    private final String UPD_CT_LOC_STMT = "UPDATE city "
            + "SET longitude=?, latitude=?, error=NULL, located=1 "
            + "where id=?";

    private final String CRT_WTH_FCT_STMT = "INSERT INTO temperature_forecast "
            + "(city_id, max_temperature, min_temperature, rain_sum, sunrise, sunset, day)\n "
            + "values (?, ?, ?, ?, FROM_UNIXTIME(?), FROM_UNIXTIME(?), FROM_UNIXTIME(?))";

    private final String CRT_WTH_FCT_ERR_STMT = "INSERT INTO forecast_error "
            + "(city_id, date, error)\n "
            + "values (?, FROM_UNIXTIME(?), ?)";

    private final String UPD_CT_LOC__ERR_STMT = "UPDATE city "
            + "SET error=?, invalid=?, number_of_tries=number_of_tries+1 "
            + "where id=?";

    private final String GT_CT_STMT = "SELECT id,name,country "
            + "FROM city "
            + "where located=? and invalid=?";

    private final String GT_LOC_STMT = "SELECT id,longitude,latitude "
            + "FROM city "
            + "where located=1 and invalid=0";

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

    public void updateCityLocations(Collection<CityLocation> cls) throws SQLException {
        try (Connection con = getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(UPD_CT_LOC_STMT)) {
                for (var cl : cls) {
                    stmt.setFloat(1, cl.getLongitude());
                    stmt.setFloat(2, cl.getLatitude());
                    stmt.setInt(3, cl.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    public void addWeatherForecasts(Collection<WeatherForecast> wfs) throws SQLException {
        try (Connection con = getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(CRT_WTH_FCT_STMT)) {
                for (var wf : wfs) {
                    stmt.setInt(1, wf.getCityId());
                    stmt.setFloat(2, wf.getMaxTemp());
                    stmt.setFloat(3, wf.getMinTemp());
                    stmt.setFloat(4, wf.getRainSum());
                    stmt.setLong(5, wf.getSunrise());
                    stmt.setLong(6, wf.getSunset());
                    stmt.setLong(7, wf.getDay());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    public void addForecastErrors(Collection<ForecastError> fes) throws SQLException {
        try (Connection con = getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(CRT_WTH_FCT_ERR_STMT)) {
                for (var fe : fes) {
                    stmt.setInt(1, fe.getCityId());
                    stmt.setLong(2, fe.getUnixTime());
                    stmt.setString(3, fe.getError());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    public void logCityLocationErrors(Collection<CityError> errs) throws SQLException {
        try (Connection con = getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(UPD_CT_LOC__ERR_STMT)) {
                for (var err : errs) {
                    stmt.setString(1, err.getError());
                    stmt.setBoolean(2, err.isInvalid());
                    stmt.setInt(3, err.getId());
                    stmt.addBatch();
                }
                stmt.executeBatch();
            }
        }
    }

    private List<CitySimple> getListOfCities(Connection con,
            boolean located, boolean invalid)
            throws SQLException {
        List<CitySimple> cities = new ArrayList<>();
        try (PreparedStatement stmt = con.prepareStatement(GT_CT_STMT)) {
            stmt.setBoolean(1, located);
            stmt.setBoolean(2, invalid);
            try (ResultSet resultSet = stmt.executeQuery()) {
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
            try (ResultSet resultSet = stmt.executeQuery(GT_LOC_STMT)) {
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

    private Connection getConnection() throws SQLException {
        return DriverManager
                .getConnection(this.url, this.username, this.password);
    }

}
