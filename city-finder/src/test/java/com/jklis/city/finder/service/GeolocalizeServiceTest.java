package com.jklis.city.finder.service;

import com.jklis.database.entity.CityError;
import com.jklis.database.entity.CityLocation;
import com.jklis.database.entity.CitySimple;
import io.vavr.control.Either;
import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.util.stream.Stream;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mockito;

@ExtendWith(MockitoExtension.class)
public class GeolocalizeServiceTest {

    private final static int CITY_ID = 1;
    private final static float LATITUDE = 50.1F;
    private final static float LONGITUDE = 50.2F;
    private final static String JSON_RESULT = "[{\"latitude\": "
            + Float.toString(LATITUDE) + ", \"longitude\": "
            + Float.toString(LONGITUDE) + "}]";
    private final static String MALFORMED_JSON = "{\"test\": \"test,}";

    @ParameterizedTest
    @MethodSource("httpClientExcepions")
    public void methodReturnsLenientCityErrorWhenHttpClientThrowsCheckedException(Exception e)
            throws IOException, InterruptedException {
        HttpClient clientMock = Mockito.mock(HttpClient.class);
        Mockito.when(clientMock.send(any(HttpRequest.class), any()))
                .thenThrow(e);
        GeolocalizeService service = new GeolocalizeService(clientMock);
        Either<CityError, CityLocation> result = service.geolocalize(testInput());
        isLenientCityError(result);
    }

    @Test
    public void methodReturnsInvalidatingCityErrorWhenHttpClientReturnsMalformedJSON()
            throws IOException, InterruptedException {
        HttpResponse<String> response = mockCorrectResponse();
        Mockito.when(response.body())
                .thenReturn(MALFORMED_JSON);
        Either<CityError, CityLocation> result = runWithResponse(response);
        isInvalidatingCityError(result);
    }

    @ParameterizedTest
    @ValueSource(ints = {100, 300, 400, 404, 500, 1})
    public void methodReturnsLenientCityErrorWhenHttpClientReturnsInvalidStatusCode(int invalidStatusCode)
            throws IOException, InterruptedException {
         HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.statusCode())
                .thenReturn(invalidStatusCode);
        Either<CityError, CityLocation> result = runWithResponse(response);
        isLenientCityError(result);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "[]", "[ ]"})
    public void methodReturnsLenientCityErrorWhenHttpClientReturnsEmptyResponse(String responseBody)
            throws IOException, InterruptedException {
        HttpResponse<String> response = mockCorrectResponse();
        Mockito.when(response.body())
                .thenReturn(responseBody);
        Either<CityError, CityLocation> result = runWithResponse(response);
        isLenientCityError(result);
    }

    @Test
    public void methodReturnsCorrectCityLocation() throws IOException, InterruptedException {
        Either<CityError, CityLocation> result = runWithResponse(mockCorrectResponse());
        Assertions.assertTrue(result.isRight());
        CityLocation location = result.get();
        assertThat(location.getId(), equalTo(CITY_ID));
        assertThat(location.getLongitude(), equalTo(LONGITUDE));
        assertThat(location.getLatitude(), equalTo(LATITUDE));
    }

    private Either<CityError, CityLocation>
            runWithResponse(HttpResponse<String> response)
            throws IOException, InterruptedException {
        HttpClient clientMock = Mockito.mock(HttpClient.class);
        Mockito.when(clientMock.send(any(HttpRequest.class),
                Mockito.<BodyHandler<String>>any()))
                .thenReturn(response);
        GeolocalizeService service = new GeolocalizeService(clientMock);
        return service.geolocalize(testInput());
    }

    private HttpResponse<String> mockCorrectResponse() {
        HttpResponse<String> response = Mockito.mock(HttpResponse.class);
        Mockito.when(response.statusCode())
                .thenReturn(200);
        Mockito.when(response.body())
                .thenReturn(JSON_RESULT);
        return response;
    }

    private void isInvalidatingCityError(Either<CityError, CityLocation> result) {
        isCityError(result, true);
    }

    private void isLenientCityError(Either<CityError, CityLocation> result) {
        isCityError (result, false);
    }

    private void isCityError(Either<CityError, CityLocation> result, boolean invalidate) {
        Assertions.assertTrue(result.isLeft());
        CityError cityError = result.getLeft();
        assertThat(cityError.getId(), equalTo(CITY_ID));
        assertThat(cityError.isInvalid(), is(invalidate));
    }

    private CitySimple testInput() {
        CitySimple citySimple = new CitySimple();
        citySimple.setId(CITY_ID);
        citySimple.setName("TestName");
        citySimple.setCountry("testCountry");
        return citySimple;
    }

    static Stream<Arguments> httpClientExcepions() {
        return Stream.of(
                Arguments.of(new IOException()),
                Arguments.of(new InterruptedException())
        );
    }

}
