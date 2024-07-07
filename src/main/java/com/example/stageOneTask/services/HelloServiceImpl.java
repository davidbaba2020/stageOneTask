package com.example.stageOneTask.services;

import com.example.stageOneTask.response.GenericResponse;
import com.example.stageOneTask.response.WeatherResponse;
import com.google.gson.Gson;
import io.ipgeolocation.api.IPGeolocationAPI;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
@Slf4j
public class HelloServiceImpl implements HelloService {

    @Value("${openweathermap.url}")
    private String openWeatherMapUrl;

    @Value("${appid.openweather}")
    private String openWeatherAppid;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    private final IPGeolocationAPI api = new IPGeolocationAPI("e556e0f5974e4445a6c3c891e41ef165");

    @Override
    public GenericResponse getGreeting(String visitorName) throws IOException {
        String city = api.getGeolocation().getCity();
        WeatherResponse weather = getWeatherForNigeria(city);
        double intTemperature = (weather.getMain().getTemp());
        String temperature = String.valueOf(intTemperature);

        String greeting = String.format("Hello, %s!, the temperature is %s degrees Celsius in %s", visitorName, temperature, city);

        return GenericResponse.builder()
                .client_ip(api.getGeolocation().getIPAddress())
                .location(city)
                .greeting(greeting)
                .build();
    }

    private WeatherResponse getWeatherForNigeria(String myLocation) throws IOException {
        Request request = new Request.Builder()
                .url(openWeatherMapUrl+"?q="+myLocation+"&appid="+openWeatherAppid+"&units=metric")
                .get()
                .build();
        try (Response response = client.newCall(request).execute()) {
            log.info("The weather response, {}", response);
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            assert response.body() != null;
            String jsonResponse = response.body().string();
            return gson.fromJson(jsonResponse, WeatherResponse.class);
        }
    }

}
