package com.good.boy.husky.weather.finder.service;

import com.good.boy.husky.database.entity.CityLocation;
import com.good.boy.husky.database.entity.ForecastError;
import com.good.boy.husky.database.entity.WeatherForecast;
import io.vavr.control.Either;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    private static final int CITY_ID = 12;
    private static final long TIME = 1748383200l;
    private static final long SUNRISE = 1748399924l;
    private static final long SUNSET = 1748457386l;
    private static final float TEMP_MAX = 23.3f;
    private static final float TEMP_MIN = 10.0f;
    private static final float RAIN_SUM = 1.81f;
    private static final String JSON_RESPONSE = String.format("{`daily`:{`time`:[%d],"
            + "`temperature_2m_max`:[23.3], `temperature_2m_min`:[10.0],`sunrise`:[1748399924],"
            + "`sunset`:[1748457386],`rain_sum`:[1.81]}}", TIME, TEMP_MAX,
            TEMP_MIN, SUNRISE, SUNSET, RAIN_SUM).replace('`', '"');

    @ParameterizedTest
    @MethodSource("httpClientExcepions")
    public void methodReturnsForecastErrorIfAHttpClientExceptionIsCaught(Exception e) throws IOException, InterruptedException {
        HttpClient clientMock = Mockito.mock(HttpClient.class);
        Mockito.when(clientMock.send(any(HttpRequest.class), any()))
                .thenThrow(e);
        WeatherService service = new WeatherService(clientMock);
        Either<ForecastError, WeatherForecast> result = service.getWeatherDetails(testInput());
        Assertions.assertTrue(result.isLeft());
        ForecastError error = result.getLeft();
        assertThat(error.getCityId(), equalTo(CITY_ID));
    }

    @ParameterizedTest
    @ValueSource(ints = {100, 300, 400, 404, 500, 1})
    public void methodReturnsForecastErrorIfResponseStatusCodeIsInvalid(int invalidStatusCode)
            throws IOException, InterruptedException {
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.statusCode())
                .thenReturn(invalidStatusCode);
        Either<ForecastError, WeatherForecast> result = runWithResponse(response);
        Assertions.assertTrue(result.isLeft());
        ForecastError error = result.getLeft();
        assertThat(error.getCityId(), equalTo(CITY_ID));
    }
    
    
    @Test
    public void methodReturnsCorrectWeatherForecast() throws IOException, InterruptedException {
        Either<ForecastError, WeatherForecast> result = runWithResponse(mockCorrectResponse());
        Assertions.assertTrue(result.isRight());
        WeatherForecast forecast = result.get();
        assertThat(forecast.getCityId(), equalTo(CITY_ID));
        assertThat(forecast.getDay(), equalTo(TIME));
        assertThat(forecast.getSunrise(), equalTo(SUNRISE));
        assertThat(forecast.getSunset(), equalTo(SUNSET));
        assertThat(forecast.getMaxTemp(), equalTo(TEMP_MAX));
        assertThat(forecast.getMinTemp(), equalTo(TEMP_MIN));
        assertThat(forecast.getRainSum(), equalTo(RAIN_SUM));
    }

    private Either<ForecastError, WeatherForecast> runWithResponse(HttpResponse<String> response)
            throws IOException, InterruptedException {
        HttpClient clientMock = Mockito.mock(HttpClient.class);
        Mockito.when(clientMock.send(any(HttpRequest.class),
                Mockito.<HttpResponse.BodyHandler<String>>any()))
                .thenReturn(response);
        WeatherService service = new WeatherService(clientMock);
        return service.getWeatherDetails(testInput());
    }

    private HttpResponse<String> mockCorrectResponse() {
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.statusCode())
                .thenReturn(200);
        Mockito.when(response.body())
                .thenReturn(JSON_RESPONSE);
        return response;
    }

    private CityLocation testInput() {
        return new CityLocation.CityLocationBuilder()
                .id(CITY_ID)
                .latitude(1f)
                .longitude(1f).build();
    }

    static Stream<Arguments> httpClientExcepions() {
        return Stream.of(
                Arguments.of(new IOException()),
                Arguments.of(new InterruptedException())
        );
    }

}
