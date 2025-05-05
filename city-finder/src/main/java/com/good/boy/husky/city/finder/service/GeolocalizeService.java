package com.good.boy.husky.city.finder.service;

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
import java.util.logging.Level;
import java.util.logging.Logger;

public class GeolocalizeService {

    private static final int REQUEST_TIMEOUT = 10;

    private final HttpClient client;

    public GeolocalizeService() {
        this.client = HttpClient
                .newBuilder()
                .build();
    }

    public Either<CityError, CityLocation> geolocalize(CitySimple city) {
        URI uri;
        try {
            uri = new URI(
                    String.format(
                            "https://api.api-ninjas.com/v1/geocoding?city=%s&country=%s",
                            city.getName(), city.getCountry()
                    )
            );
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(uri)
                    .timeout(Duration.of(REQUEST_TIMEOUT, SECONDS))
                    .header("X-Api-Key", "oFlgWSh/uWCO8shh0w9+3w==WxnuhK9QmkYdqt79")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println(response.body());
        } catch (URISyntaxException ex) {
            Logger.getLogger(GeolocalizeService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(GeolocalizeService.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(GeolocalizeService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

}
