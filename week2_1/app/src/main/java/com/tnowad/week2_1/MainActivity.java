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


        if(isNetworkAvailable()){
            new CheckInternetTask().execute("https://goohjhjhgle.com");
        }else{
            statusText.setText("Khong the ket noi mang");
        }
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }



    private boolean isNetworkAvailable(){
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

    private class CheckInternetTask extends AsyncTask<String, Void, Boolean>{

        @Override
        protected Boolean doInBackground(String... urls) {
            try{
                HttpURLConnection urlConnection = (HttpURLConnection) new URL(urls[0]).openConnection();
                urlConnection.setRequestMethod("HEAD");
                urlConnection.setConnectTimeout(1000);
                urlConnection.setReadTimeout(1000);
                urlConnection.connect();
                int responseCode = urlConnection.getResponseCode();

                return (responseCode == 200);
            } catch (IOException e){
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if(result){
                statusText.setText("ket noi voi google thanh cong");
            }else{
                statusText.setText("khong the ket noi voi gg");
            }
        }
    }

}