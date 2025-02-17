package com.tnowad.week3_1;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private TextView countTextView;
    private Button startButton;
    private ProgressBar progressBar;
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        countTextView = findViewById(R.id.textView_count);
        startButton = findViewById(R.id.button_start);
        progressBar = findViewById(R.id.progressBar);

        startButton.setOnClickListener(v -> startCounting());

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void startCounting() {
        new Thread(() -> {
            for (int i = 0; i <= 10; i++) {
                int count = i;
                handler.post(() -> countTextView.setText(String.valueOf(count)));
                handler.post(() -> progressBar.setProgress(count * 10));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            handler.post(() -> countTextView.setText("Completed"));       }).start();
    }
}