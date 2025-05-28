package com.good.boy.husky.weather.finder.service;

import com.good.boy.husky.database.entity.CityLocation;
import com.good.boy.husky.database.entity.WeatherForecast;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Timestamp;
import java.time.Duration;
import static java.time.temporal.ChronoUnit.SECONDS;
import java.util.Date;
import org.json.JSONObject;

public class WeatherService {

    private static final int REQUEST_TIMEOUT = 10;

    private final HttpClient client;
    
    private final static String uriTemplate = "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f"
            + "&daily=temperature_2m_max,temperature_2m_min,sunrise,sunset,rain_sum&timezone=auto&forecast_days=1"
            + "&format=json&timeformat=unixtime";

    public WeatherService() {
        this.client = HttpClient
                .newBuilder()
                .build();
    }

    WeatherService(HttpClient client) {
        this.client = client;
    }

    public WeatherForecast getWeatherDetails(CityLocation location) 
            throws URISyntaxException, IOException, InterruptedException {
        String uriString = String.format(uriTemplate, location.getLatitude(), location.getLongitude());
        URI uri = new URI(uriString);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .timeout(Duration.of(REQUEST_TIMEOUT, SECONDS))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONObject daily = new JSONObject(response.body()).getJSONObject("daily");
        return new WeatherForecast.WeatherForecastBuilder()
                .cityId(location.getId())
                .maxTemp(daily.getJSONArray("temperature_2m_max").getFloat(0))
                .minTemp(daily.getJSONArray("temperature_2m_min").getFloat(0))
                .rainSum(daily.getJSONArray("rain_sum").getFloat(0))
                .sunrise(daily.getJSONArray("sunrise").getLong(0))
                .sunset(daily.getJSONArray("sunset").getLong(0))
                .day(daily.getJSONArray("time").getLong(0)).build();
    }

}
