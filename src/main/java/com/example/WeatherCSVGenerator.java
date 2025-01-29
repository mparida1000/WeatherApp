package com.example;

import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;
import org.json.JSONObject;

public class WeatherCSVGenerator {
    private static final String API_KEY = "ade48d6ddee0fcf184d25c174766e12f";  // Replace with OpenWeatherMap API key
    private static final String CITY = "Bangalore";  // Change city as needed
    private static final String BASE_URL = "https://api.openweathermap.org/data/2.5/weather?q=" + CITY + "&appid=" + API_KEY + "&units=metric";

    public static void main(String[] args) {
        try {
            String jsonResponse = getWeatherData(BASE_URL);
            if (jsonResponse != null) {
                JSONObject jsonObject = new JSONObject(jsonResponse);
                String cityName = jsonObject.getString("name");
                JSONObject main = jsonObject.getJSONObject("main");
                double temperature = main.getDouble("temp");
                double pressure = main.getDouble("pressure");
                double humidity = main.getDouble("humidity");
                JSONObject wind = jsonObject.getJSONObject("wind");
                double windSpeed = wind.getDouble("speed");

                writeCSV(cityName, temperature, pressure, humidity, windSpeed);
                System.out.println("Weather data saved to weather_data.csv");
            } else {
                System.out.println("Failed to fetch weather data.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getWeatherData(String apiUrl) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();

            int responseCode = conn.getResponseCode();
            if (responseCode != 200) {
                return null;
            }

            Scanner scanner = new Scanner(url.openStream());
            StringBuilder jsonResponse = new StringBuilder();
            while (scanner.hasNext()) {
                jsonResponse.append(scanner.nextLine());
            }
            scanner.close();
            return jsonResponse.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static void writeCSV(String city, double temp, double pressure, double humidity, double windSpeed) {
        String csvFile = System.getProperty("user.home") + "/Desktop/weather_data.csv";
        try (FileWriter writer = new FileWriter(csvFile)) {
            writer.append("City,Temperature (°C),Pressure (hPa),Humidity (%),Wind Speed (m/s)\n");
            writer.append(city).append(",")
                    .append(String.valueOf(temp)).append(",")
                    .append(String.valueOf(pressure)).append(",")
                    .append(String.valueOf(humidity)).append(",")
                    .append(String.valueOf(windSpeed)).append("\n");

            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

