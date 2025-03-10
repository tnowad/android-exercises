package com.tnowad.week6_4;

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

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText edtCurrency;
    private TextView txtResult;
    private Button btnConvert;
    private static final String API_KEY = "08d617f260035a0613c8f10e";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtCurrency = findViewById(R.id.edtCurrency);
        txtResult = findViewById(R.id.txtResult);
        btnConvert = findViewById(R.id.btnConvert);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnConvert.setOnClickListener(v -> {
            String currencyCode = edtCurrency.getText().toString().toUpperCase().trim();
            if (!currencyCode.isEmpty()) {
                new ConvertCurrencyTask(txtResult, currencyCode).execute();
            } else {
                txtResult.setText("Enter a valid currency code.");
            }
        });
    }

    private static class ConvertCurrencyTask extends AsyncTask<Void, Void, String> {
        private static final String API_KEY = "08d617f260035a0613c8f10e";
        private final TextView txtResult;
        private final String currencyCode;

        ConvertCurrencyTask(TextView txtResult, String currencyCode) {
            this.txtResult = txtResult;
            this.currencyCode = currencyCode;
        }

        @Override
        protected String doInBackground(Void... voids) {
            String urlString = "https://v6.exchangerate-api.com/v6/" + API_KEY + "/latest/USD";
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
                    return "{\"error\":\"Invalid response\"}";
                }
            } catch (Exception e) {
                Log.e("ConvertCurrencyTask", "Error fetching exchange rate", e);
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
                    txtResult.setText(jsonObject.getString("error"));
                    return;
                }

                JSONObject rates = jsonObject.getJSONObject("conversion_rates");
                if (rates.has(currencyCode)) {
                    double rate = rates.getDouble(currencyCode);
                    txtResult.setText("1 USD = " + rate + " " + currencyCode);
                } else {
                    txtResult.setText("Invalid currency code");
                }
            } catch (Exception e) {
                Log.e("ConvertCurrencyTask", "Error parsing JSON", e);
                txtResult.setText("Error parsing data");
            }
        }
    }
}
