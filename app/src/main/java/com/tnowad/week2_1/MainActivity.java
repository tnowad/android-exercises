package com.tnowad.week2_1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private TextView statusText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        statusText = findViewById(R.id.statusText);

        if(isNetWorkAvailable()) {
            new CheckInternetTask().execute("https://www.google.com");
        } else {
            statusText.setText("No Internet!");
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private boolean isNetWorkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo !=null && activeNetworkInfo.isConnected();
    }

    private class CheckInternetTask extends AsyncTask<String,Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
                urlConnection.setRequestMethod("HEAD");
                urlConnection.setConnectTimeout(3000);
                urlConnection.setReadTimeout(3000);
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();
                return (responseCode==200);
            }catch (IOException error) {
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);
            if (result) {
                statusText.setText("Connect to Google successfully!");
            } else {
                statusText.setText("Connect to Google failed!");

            }
        }
    }
}