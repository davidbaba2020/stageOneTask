package com.example.stageOneTask.services;

import com.example.stageOneTask.response.GenericResponse;
import com.example.stageOneTask.response.WeatherResponse;
import com.google.gson.Gson;
import io.ipgeolocation.api.IPGeolocationAPI;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
    public GenericResponse getGreeting(String visitorName, HttpServletRequest request) throws IOException {
        String ipAddress = getClientIp(request);
        String city = getClientIp(request);
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

private static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            ipAddress = request.getRemoteAddr();
        }

        // Log IP address for debugging
        System.out.println("Client IP Address: " + ipAddress);

        return ipAddress;
    }


        private static String getCityFromIp(String ipAddress) {
            String apiKey = "YOUR_API_KEY"; // Replace with your API key
            String apiUrl = String.format("http://api.ipapi.com/%s?access_key=%s", ipAddress, apiKey);

            try {
                URL url = new URL(apiUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Accept", "application/json");

                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                connection.disconnect();

                JSONObject json = new JSONObject(content.toString());
                return json.optString("city", "NO Such City");
            } catch (Exception e) {
                e.printStackTrace();
                return "NO Such City";
            }
        }


}
