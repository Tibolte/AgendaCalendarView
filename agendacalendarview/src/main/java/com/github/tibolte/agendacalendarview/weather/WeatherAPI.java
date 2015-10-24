package com.github.tibolte.agendacalendarview.weather;


import com.github.tibolte.agendacalendarview.weather.models.Forecast;
import com.github.tibolte.agendacalendarview.weather.models.Request;

import java.util.Map;

import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;
import retrofit.http.QueryMap;
import rx.Observable;

public interface WeatherAPI {
    String API_KEY = "1337e362ca142f1e855ce277a3391ad2";

    String ENDPOINT = "https://api.forecast.io/forecast/" + API_KEY;

    /**
     * Ask for a forecast.
     *
     * @param params Latitude, Longitude and time for the request.
     * @param query  Configurations (units, language, etc.) for the request.
     * @return The requested forecast.
     */
    @Headers("Content-Type: application/json")
    @GET("/{request}")
    Observable<Forecast> getForecast(@Path("request") Request params, @QueryMap Map<String, String> query);
}
