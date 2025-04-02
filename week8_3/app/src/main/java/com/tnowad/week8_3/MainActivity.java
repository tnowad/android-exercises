package com.tnowad.week8_3;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.util.Xml;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> titles = new ArrayList<>();
    private ExecutorService executorService;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, titles);
        listView.setAdapter(adapter);

        executorService = Executors.newSingleThreadExecutor();
        Log.d(TAG, "Fetching RSS feed...");
        fetchRSS("https://thanhnien.vn/rss/home.rss");
    }

    private void fetchRSS(String urlString) {
        executorService.execute(() -> {
            ArrayList<String> fetchedTitles = new ArrayList<>();
            try {
                Log.d(TAG, "Connecting to: " + urlString);
                URL url = new URL(urlString);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android)");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.connect();

                int responseCode = connection.getResponseCode();
                Log.d(TAG, "Response Code: " + responseCode);

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream inputStream = connection.getInputStream();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                    StringBuilder rawXml = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        rawXml.append(line).append("\n");
                    }
                    Log.d(TAG, "Raw XML Response:\n" + rawXml.toString());

                    XmlPullParser parser = Xml.newPullParser();
                    parser.setInput(new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));

                    int eventType = parser.getEventType();
                    boolean insideItem = false;
                    String title = null;

                    while (eventType != XmlPullParser.END_DOCUMENT) {
                        if (eventType == XmlPullParser.START_TAG) {
                            String tagName = parser.getName();
                            Log.d(TAG, "Parsing Tag: " + tagName);

                            if ("item".equalsIgnoreCase(tagName) || "entry".equalsIgnoreCase(tagName)) {
                                insideItem = true;
                            } else if (insideItem && "title".equalsIgnoreCase(tagName)) {
                                title = parser.nextText();
                                Log.d(TAG, "Fetched Title: " + title);
                            }
                        } else if (eventType == XmlPullParser.END_TAG && "item".equalsIgnoreCase(parser.getName())) {
                            insideItem = false;
                            if (title != null) {
                                fetchedTitles.add(title);
                            }
                        }
                        eventType = parser.next();
                    }

                    inputStream.close();

                    handler.post(() -> {
                        titles.clear();
                        titles.addAll(fetchedTitles);
                        adapter.notifyDataSetChanged();
                        Log.d(TAG, "Updated UI with " + titles.size() + " items.");
                    });

                } else {
                    Log.e(TAG, "Failed to fetch RSS. Response code: " + responseCode);
                }

            } catch (IOException | XmlPullParserException e) {
                Log.e(TAG, "Error fetching RSS feed", e);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        executorService.shutdown();
        Log.d(TAG, "Executor service shut down.");
    }
}
