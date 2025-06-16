package com.jklis.database.service;

import com.jklis.database.service.DatabaseService;
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
import java.util.List;
import java.util.stream.Stream;
import org.junit.ClassRule;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.hasItem;
import static org.testcontainers.shaded.org.hamcrest.Matchers.equalTo;
import static org.testcontainers.shaded.org.hamcrest.Matchers.hasProperty;
import static org.testcontainers.shaded.org.hamcrest.Matchers.is;
import static org.testcontainers.shaded.org.hamcrest.Matchers.containsInAnyOrder;
import static org.testcontainers.shaded.org.hamcrest.Matchers.allOf;
import static org.testcontainers.shaded.org.hamcrest.Matchers.emptyOrNullString;
import static org.testcontainers.shaded.org.hamcrest.Matchers.not;

@Testcontainers
public class DatabaseServiceTest {

    private static DatabaseService service;
    private static final String LOCATED_CITY_NAME = "located_city";
    private static final String UNLOCATED_CITY_NAME = "unlocated_city";
    private static final String UNLOC_CITY_WITH_ERROR_MESSAGE_NAME = "unlocated_with_error_message";
    private static final float C_LONG = 50.1f;
    private static final float C_LATI = 50.2f;
    private static final float UPD_C_LONG = 50.3f;
    private static final float UPD_C_LATI = 50.4f;
    private static final float MAX_TEMP = 10.1f;
    private static final float MIN_TEMP = 10.2f;
    private static final float RAIN_SUM = 10.3f;
    private static final long SUNRISE = 1749734752l;
    private static final long SUNSET = 1749734753l;
    private static final long DAY = 1749734754l;
    private static final long ERROR_DATE = 1749734755l;
    private static final String ERROR_MESSAGE = "error_message";
    private static final String ERROR_PARAM_NAME = "error";
    private static final String NUMBER_OF_TRIES_PARAM_NAME = "number_of_tries";
    private static final String INVALID_PARAM_NAME = "invalid";

    @ClassRule
    private final static MySQLContainer<?> container = new MySQLContainer<>("mysql:latest")
            .withInitScript("init_db.sql");

    @BeforeAll
    public static void beforeAll() throws SQLException {
        container.start();
        service = new DatabaseService(container.getJdbcUrl(),
                container.getUsername(), container.getPassword());
    }

    @BeforeEach
    public void beforeEach() throws SQLException {
        createTestValues();
    }

    @AfterEach
    public void afterEach() throws SQLException {
        cleanDatabase();
    }

    @Test
    public void getListOfLocatedCitiesReturnsLocatedCities() throws SQLException {
        List<CitySimple> locatedCities = service.getListOfLocatedCities();
        assertThat(locatedCities, containsInAnyOrder(
                hasProperty("name", equalTo(LOCATED_CITY_NAME))
        ));
    }

    @Test
    public void getListOfUnlocatedCitiesReturnsUnlocatedCities() throws SQLException {
        List<CitySimple> unlocatedCities = service.getListOfUnlocatedCities();
        assertThat(unlocatedCities, containsInAnyOrder(
                hasProperty("name", equalTo(UNLOCATED_CITY_NAME)),
                hasProperty("name", equalTo(UNLOC_CITY_WITH_ERROR_MESSAGE_NAME))
        ));
    }

    @Test
    public void getListOfCityLocationReturnsLocationsOfLocatedCities() throws SQLException {
        int locatedCityId = service.getListOfLocatedCities().get(0).getId();

        assertThat(service.getListOfCityLocations(),
                hasItem(
                        allOf(
                                hasProperty("id", is(locatedCityId)),
                                hasProperty("longitude", is(C_LONG)),
                                hasProperty("latitude", equalTo(C_LATI))
                        )
                )
        );
    }

    @Test
    public void updateCityLocationUpdatesCityLocation() throws SQLException {
        int id = updateLocation(LOCATED_CITY_NAME);
        assertThat(service.getListOfCityLocations(),
                hasItem(
                        allOf(
                                hasProperty("id", is(id)),
                                hasProperty("longitude", is(UPD_C_LONG)),
                                hasProperty("latitude", equalTo(UPD_C_LATI))
                        )
                )
        );
    }

    @Test
    public void updateCityLocationSetsLocatedToTrue() throws SQLException {
        int id = updateLocation(UNLOCATED_CITY_NAME);
        assertThat(service.getListOfLocatedCities(),
                hasItem(
                        hasProperty("id", is(id))
                )
        );
    }

    @Test
    public void updateCityLocationRemovesErrorMessage() throws SQLException {
        assertThat(getErrorMessage(UNLOC_CITY_WITH_ERROR_MESSAGE_NAME),
                is(not(emptyOrNullString())));
        updateLocation(UNLOC_CITY_WITH_ERROR_MESSAGE_NAME);
        assertThat(getErrorMessage(UNLOC_CITY_WITH_ERROR_MESSAGE_NAME),
                is(emptyOrNullString()));
    }

    @Test
    public void addWeatherForecastSavesWeatherForecast() throws SQLException {
        int cityId = getCityId(LOCATED_CITY_NAME);
        var wf = new WeatherForecast.WeatherForecastBuilder()
                .cityId(cityId)
                .maxTemp(MAX_TEMP)
                .minTemp(MIN_TEMP)
                .rainSum(RAIN_SUM)
                .sunrise(SUNRISE)
                .sunset(SUNSET)
                .day(DAY)
                .build();
        service.addWeatherForecast(wf);
        assertThat(
                weatherForecastExists(cityId, MAX_TEMP, MIN_TEMP,
                        RAIN_SUM, SUNRISE, SUNSET, DAY),
                is(true));
    }

    @Test
    public void addForecastErrorSavesForecastError() throws SQLException {
        int cityId = getCityId(LOCATED_CITY_NAME);
        var fe = new ForecastError()
                .setCityId(cityId)
                .setError(ERROR_MESSAGE)
                .setUnixTime(ERROR_DATE);
        service.addForecastError(fe);
        assertThat(
                forecastErrorExist(cityId, ERROR_MESSAGE, ERROR_DATE),
                is(true));
    }

    @Test
    public void loggingCityConnectionErrorIncreasesNumberOfTries() throws SQLException {
        int numberOfTriesBefore = getNumberOfTries(LOCATED_CITY_NAME);
        int cityId = getCityId(LOCATED_CITY_NAME);
        service.logCityLocationError(new CityError(cityId, ERROR_MESSAGE, false));
        assertThat(
                getNumberOfTries(LOCATED_CITY_NAME),
                equalTo(numberOfTriesBefore + 1)
        );
    }

    @Test
    public void loggingCityConnectionErrorWithInvalidTrueMakesCityInvalid() throws SQLException {
        int cityId = getCityId(LOCATED_CITY_NAME);
        service.logCityLocationError(new CityError(cityId, ERROR_MESSAGE, true));
        assertThat(
                isInvalid(LOCATED_CITY_NAME),
                is(true)
        );
    }

    private static void createTestValues() throws SQLException {
        try (var con = getConnection()) {
            try (Statement stmt = con.createStatement()) {
                String insertSql = String.format("INSERT INTO city (name, country, longitude, latitude, error, located)\n "
                        + "values ('%s', 'country', %f, %f, null, true),\n"
                        + "('%s', 'country', NULL, NULL, NULL, false),\n"
                        + "('%s', 'country', NULL, NULL, 'test_error', false);",
                        LOCATED_CITY_NAME, C_LONG, C_LATI,
                        UNLOCATED_CITY_NAME, UNLOC_CITY_WITH_ERROR_MESSAGE_NAME);
                stmt.executeUpdate(insertSql);
                stmt.close();
            }
        }
    }

    private int getCityId(String cityName) throws SQLException {
        return Stream.concat(
                service.getListOfLocatedCities().stream(),
                service.getListOfUnlocatedCities().stream())
                .filter(c -> c.getName().equals(cityName))
                .findAny().get().getId();
    }

    private int updateLocation(String cityName) throws SQLException {
        int id = getCityId(cityName);
        service.updateCityLocation(
                new CityLocation.CityLocationBuilder()
                        .id(id).latitude(UPD_C_LATI).longitude(UPD_C_LONG)
                        .build()
        );
        return id;
    }

    private String getErrorMessage(String cityName) throws SQLException {
        return getCityParam(cityName, ERROR_PARAM_NAME, String.class);
    }

    private int getNumberOfTries(String cityName) throws SQLException {
        return getCityParam(cityName, NUMBER_OF_TRIES_PARAM_NAME, Integer.class);
    }

    private boolean isInvalid(String cityName) throws SQLException {
        return getCityParam(cityName, INVALID_PARAM_NAME, Boolean.class);
    }

    private <T> T getCityParam(String cityName, String paramName, Class<T> rClass) throws SQLException {
        try (var con = getConnection()) {
            try (Statement stmt = con.createStatement()) {
                String selectSql = String.format("SELECT %s from city where name='%s' limit 1",
                        paramName, cityName);
                try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
                    resultSet.next();
                    return resultSet.getObject(paramName, rClass);
                }
            }
        }
    }

    private boolean weatherForecastExists(int cityId, float maxTemp, float minTemp,
            float rainsum, long sunrise, long sunset, long day) throws SQLException {
        try (var con = getConnection()) {
            try (Statement stmt = con.createStatement()) {
                String selectSql = String.format("SELECT 1 from temperature_forecast " // floats have been concatenated with plus
                        + "where city_id=%d and max_temperature LIKE " + maxTemp
                        + // signs to avoid trailing spaces.
                        " and min_temperature LIKE " + minTemp
                        + " and rain_sum LIKE " + rainsum + " and sunrise=FROM_UNIXTIME(%d) "
                        + "and sunset=FROM_UNIXTIME(%d) and day=FROM_UNIXTIME(%d, '%%Y-%%m-%%d')",
                        cityId, sunrise, sunset, day);
                try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
                    return resultSet.next();
                }
            }
        }
    }

    private boolean forecastErrorExist(int cityId, String error, long date) throws SQLException {
        try (var con = getConnection()) {
            try (Statement stmt = con.createStatement()) {
                String selectSql = String.format("SELECT 1 from forecast_error "
                        + "where city_id=%d and error='%s' and date=FROM_UNIXTIME(%d)",
                        cityId, error, date);
                try (ResultSet resultSet = stmt.executeQuery(selectSql)) {
                    return resultSet.next();
                }
            }
        }
    }

    private static void cleanDatabase() throws SQLException {
        try (var con = getConnection()) {
            try (Statement stmt = con.createStatement()) {
                stmt.executeUpdate("DELETE FROM temperature_forecast");
                stmt.executeUpdate("DELETE FROM forecast_error");
                stmt.executeUpdate("DELETE FROM city");
            }
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(container.getJdbcUrl(), container.getUsername(),
                container.getPassword());
    }

}
