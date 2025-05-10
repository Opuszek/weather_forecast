package com.good.boy.husky.city.finder.service;

import com.good.boy.husky.database.utilities.Utilities;
import com.good.boy.husky.database.entity.CityError;
import com.good.boy.husky.database.entity.CityLocation;
import com.good.boy.husky.database.entity.CitySimple;
import io.vavr.control.Either;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GeolocalizeService {

    private static final int REQUEST_TIMEOUT = 10;

    private final HttpClient client;

    private final Pattern emptyJSONArray = Pattern.compile("\\s*\\[\\s*\\]\\s*");

    private final List<Class<? extends Exception>> invalidatingExceptions
            = Arrays.asList(URISyntaxException.class, JSONException.class);

    public GeolocalizeService() {
        this.client = HttpClient
                .newBuilder()
                .build();
    }

    public Either<CityError, CityLocation> geolocalize(CitySimple city) {
        String uriString = String.format(
                "https://api.api-ninjas.com/v1/geocoding?city=%s&country=%s",
                city.getName(), city.getCountry()
        );
        try {
            URI uri = new URI(uriString);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.of(REQUEST_TIMEOUT, SECONDS))
                    .header(Utilities.getProperty("api_key_header"),
                            Utilities.getProperty("api_key"))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            if (responseIsValid(response) && !responseBodyIsEmpty(response.body())) {
                JSONObject resultJson = new JSONArray(response.body())
                        .getJSONObject(0);
                CityLocation cityLocation = new CityLocation.CityLocationBuilder()
                        .id(city.getId())
                        .latitude(resultJson.getFloat("latitude"))
                        .longitude(resultJson.getFloat("longitude")).build();
                return Either.right(cityLocation);
            } else {
                return Either.left(new CityError(city.getId(),
                        String.format("Request failed with statusCode %s and responseBody %s",
                                response.statusCode(), response.body()), false));
            }
        } catch (IOException | InterruptedException | URISyntaxException
                | JSONException ex) {
            return Either.left(new CityError(city.getId(),
                    ex.getClass().getName(),
                    this.invalidatingExceptions.contains(ex.getClass())));
        }
    }
    
    private boolean responseIsValid(HttpResponse<String> response) {
        return response.statusCode() >= 200 && response.statusCode() < 300;
    }

    private boolean responseBodyIsEmpty(String json) {
        return json.isBlank() || this.emptyJSONArray.matcher(json).matches();
    }

}
