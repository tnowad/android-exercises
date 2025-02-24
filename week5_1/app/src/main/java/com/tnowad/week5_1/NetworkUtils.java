package com.tnowad.week5_1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONObject;

public class NetworkUtils {
    private static final String API_URL = "https://restcountries.com/v3.1/all";

    public static ArrayList<Country> fetchCountries() {
        ArrayList<Country> countries = new ArrayList<>();
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            URL url = new URL(API_URL);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }

                JSONArray jsonArray = new JSONArray(response.toString());
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String name = jsonObject.has("name") ? jsonObject.getJSONObject("name").getString("common") : "Unknown";
                    String capital = jsonObject.has("capital") ? jsonObject.getJSONArray("capital").getString(0) : "No Capital";
                    String region = jsonObject.has("region") ? jsonObject.getString("region") : "No Region";
                    countries.add(new Country(capital, name, region));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception ignored) {}
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
        return countries;
    }
}

