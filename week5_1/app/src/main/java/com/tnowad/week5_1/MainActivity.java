package com.tnowad.week5_1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private Button btnFetch;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> countryNames = new ArrayList<>();
    private ExecutorService executorService;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        listView = findViewById(R.id.listView);
        btnFetch = findViewById(R.id.btnFetch);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, countryNames);
        listView.setAdapter(adapter);

        executorService = Executors.newSingleThreadExecutor();
        handler = new Handler(Looper.getMainLooper());

        btnFetch.setOnClickListener(v -> fetchCountries());
    }

    private void fetchCountries() {
        executorService.execute(() -> {
            ArrayList<Country> countries = NetworkUtils.fetchCountries();
            updateUI(countries);
        });
    }

    private void updateUI(ArrayList<Country> countries) {
        handler.post(() -> {
            countryNames.clear();
            for (Country country : countries) {
                countryNames.add(country.toString());
            }
            adapter.notifyDataSetChanged();
        });
    }
}
