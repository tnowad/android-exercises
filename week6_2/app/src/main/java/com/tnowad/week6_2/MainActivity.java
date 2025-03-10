package com.tnowad.week6_2;

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
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText edtName, edtEmail;
    private TextView txtResponse;
    private Button btnSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        txtResponse = findViewById(R.id.txtResponse);
        btnSend = findViewById(R.id.btnSend);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSend.setOnClickListener(v -> {
            String name = edtName.getText().toString();
            String email = edtEmail.getText().toString();
            new PostUserTask() {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    txtResponse.setText("Response from server: " + result);
                }
            }.execute(name, email);
        });
    }

    public class PostUserTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String name = params[0];
            String email = params[1];
            StringBuilder response = new StringBuilder();

            try {
                URL url = new URL("https://jsonplaceholder.typicode.com/users");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", name);
                jsonObject.put("email", email);

                OutputStream os = conn.getOutputStream();
                os.write(jsonObject.toString().getBytes("UTF-8"));
                os.close();


                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_CREATED || responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    reader.close();
                } else {
                    response.append("Error: ").append(responseCode);
                }
            } catch (Exception e) {
                Log.e("PostUserTask", "Error sending user data", e);
            }

            return response.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.d("PostUserTask", "Response: " + result);
        }
    }
}