package com.example.stageOneTask.services;

import com.example.stageOneTask.response.GenericResponse;
import com.example.stageOneTask.response.IpInfo;
import com.example.stageOneTask.response.WeatherResponse;
import com.google.gson.Gson;
import io.ipgeolocation.api.IPGeolocationAPI;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
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
    @Value("${ip.info.url}")
    private String ipIfoUrl;
    @Value("${ip.info.token}")
    private String opInfoToken;

    private final OkHttpClient client = new OkHttpClient();
    private final Gson gson = new Gson();
    @Override
    public GenericResponse getGreeting(String visitorName, HttpServletRequest request) throws IOException {
        IpInfo ipInfo = getIpInfo();
        log.info("My Ipinfo response, {}", ipInfo);
        String ipAddress = ipInfo.getIp();
        String city = ipInfo.getCity();
        WeatherResponse weather = getWeatherForNigeria(city);
        double intTemperature = (weather.getMain().getTemp());
        String temperature = String.valueOf(intTemperature);
        String greeting = String.format("Hello, %s!, the temperature is %s degrees Celsius in %s", visitorName, temperature, city);
        return GenericResponse.builder()
                .client_ip(ipAddress)
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

    private IpInfo getIpInfo() throws IOException {
        MediaType mediaType = MediaType.parse("text/plain");
        Request request = new Request.Builder()
                .url(ipIfoUrl+"?token="+opInfoToken)
                .build();
        Response response = client.newCall(request).execute();

        if (response.body() != null) {
            String responseBody = response.body().string();
            return gson.fromJson(responseBody, IpInfo.class);
        } else {
            throw new IOException("Failed to get IP info");
        }
    }

}
