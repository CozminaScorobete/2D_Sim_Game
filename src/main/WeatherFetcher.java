package main;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class WeatherFetcher {
    private static final String API_KEY = ""; // Replace with your API key
    private static final String[] LOCATIONS = {
        "London",  // Rainy city
        "Bogota",  // Often rainy
        "Moscow",  // Snowy
        "Toronto", // Snowy
        "Dubai",   // Always clear/sunny
        "Sao Paulo", // Mixed weather
        "Mumbai",  // Monsoon rains
        "New York", // Variable weather
        "Tokyo",
        "Costinesti"// Four seasons
    };

    private static String currentWeather = "Clear";
    private static String currentCity = LOCATIONS[0]; // Start with first city
    private static Timer weatherTimer = new Timer();
    private static Random random = new Random();
    private static int apiCallCount = 0;
    private static long lastResetTime = System.currentTimeMillis();
    static {
        // Start changing locations every 60 seconds
        weatherTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                changeLocation();
            }
        }, 0, 60000); // Change every 60 seconds
    }

    public static void changeLocation() {
        // Pick a random city
        currentCity = LOCATIONS[random.nextInt(LOCATIONS.length)];
        System.out.println("Fetching weather for: " + currentCity);
        currentWeather = fetchWeatherFromAPI(currentCity);
        System.out.println("Weather in " + currentCity + ": " + currentWeather);
    }

    private static String fetchWeatherFromAPI(String city) {
        try {
            String apiKey = ""; // 🔹 Make sure this is correct

            // Properly format city names with spaces (URL encode them)
            String formattedCity = city.replace(" ", "%20");
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastResetTime >= 30000) { // If 60 seconds have passed
                System.out.println("⏳ API calls in the last minute: " + apiCallCount);
                apiCallCount = 0; // Reset counter
                lastResetTime = currentTime;
            }

            // 🔹 INCREMENT API CALL COUNT
            apiCallCount++;
            System.out.println("🌍 Calling OpenWeather API for city: " + city);
            // Correct API call with proper encoding
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + formattedCity + "&appid=" + apiKey + "&units=metric";

            System.out.println("Fetching Weather: " + urlString); // Debugging

            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 400) {
                throw new IOException("Bad Request (400 Error) - Check City Name Format");
            }

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            return extractWeather(response.toString());

        } catch (Exception e) {
            e.printStackTrace();
            return "Clear"; // Default to clear if API fails
        }
    }


    private static String extractWeather(String jsonResponse) {
        try {
            int weatherIndex = jsonResponse.indexOf("\"weather\"");
            if (weatherIndex == -1) return "Clear";

            int descriptionIndex = jsonResponse.indexOf("\"main\":\"", weatherIndex);
            if (descriptionIndex == -1) return "Clear";

            int startIndex = descriptionIndex + 8;
            int endIndex = jsonResponse.indexOf("\"", startIndex);

            return endIndex != -1 ? jsonResponse.substring(startIndex, endIndex) : "Clear";

        } catch (Exception e) {
            e.printStackTrace();
            return "Clear";
        }
    }

    public static String getCurrentWeather() {
        return currentWeather;
    }

    public static String getCurrentCity() {
        return currentCity;
    }
}
