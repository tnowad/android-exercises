package com.tnowad.week6_3;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private EditText edtCity;
    private TextView txtWeather;
    private Button btnGetWeather;
    private static final String API_KEY = "a2c6df140276e327b29e4b62852a8762";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtCity = findViewById(R.id.edtCity);
        txtWeather = findViewById(R.id.txtWeather);
        btnGetWeather = findViewById(R.id.btnGetWeather);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnGetWeather.setOnClickListener(v -> {
            String city = edtCity.getText().toString();
            new GetWeatherTask().execute(city);
        });
    }
    public class GetWeatherTask extends AsyncTask<String, Void, String> {
        private static final String API_KEY = "a2c6df140276e327b29e4b62852a8762";

        @Override
        protected String doInBackground(String... params) {
            String city = params[0];
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + API_KEY + "&units=metric";
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL(urlString);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    response.append("{\"error\":\"Invalid city or API issue\"}");
                }

            } catch (Exception e) {
                Log.e("GetWeatherTask", "Error fetching weather data", e);
                return "{\"error\":\"Network error\"}";
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                JSONObject jsonObject = new JSONObject(result);

                if (jsonObject.has("error")) {
                    txtWeather.setText(jsonObject.getString("error"));
                    return;
                }

                String city = jsonObject.getString("name");
                double temperature = jsonObject.getJSONObject("main").getDouble("temp");

                txtWeather.setText("City: " + city + "\nTemperature: " + temperature + "°C");
            } catch (JSONException e) {
                Log.e("GetWeatherTask", "Error parsing JSON", e);
                txtWeather.setText("Error parsing data");
            }
        }
    }
}