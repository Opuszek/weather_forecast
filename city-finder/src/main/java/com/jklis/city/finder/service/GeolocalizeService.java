package com.jklis.city.finder.service;

import com.jklis.database.utilities.Utilities;
import com.jklis.database.entity.CityError;
import com.jklis.database.entity.CityLocation;
import com.jklis.database.entity.CitySimple;
import com.jklis.utilities.Either;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.Arrays;
import java.util.List;
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

    GeolocalizeService(HttpClient client) {
        this.client = client;
    }

    public Either<CityError, CityLocation> geolocalize(CitySimple city) {
        try {
            String uriString = String.format(
                    "https://api.api-ninjas.com/v1/geocoding?city=%s&country=%s",
                    URLEncoder.encode(city.getName(), "UTF-8"),
                    URLEncoder.encode(city.getCountry(), "UTF-8")
            );
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
                return Either.withRight(cityLocation);
            } else {
                return Either.withLeft(new CityError(city.getId(),
                        String.format("Request failed with statusCode %s and responseBody %s",
                                response.statusCode(), response.body()), false));
            }
        } catch (IOException | InterruptedException | URISyntaxException
                | JSONException ex) {
            return Either.withLeft(new CityError(city.getId(),
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
