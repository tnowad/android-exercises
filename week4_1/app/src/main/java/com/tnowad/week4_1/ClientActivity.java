package com.tnowad.week4_1;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientActivity extends AppCompatActivity {

    private EditText messageEditText, ipEditText, portEditText;
    private TextView chatboxTextView;
    private Button sendButton, connectButton;
    private PrintWriter out;
    private BufferedReader in;
    private Handler handler = new Handler();
    private Socket socket;
    private Thread clientThread, receiveThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_client);

        ipEditText = findViewById(R.id.ip_edit_text);
        portEditText = findViewById(R.id.port_edit_text);
        messageEditText = findViewById(R.id.message_edit_text);
        chatboxTextView = findViewById(R.id.client_chatbox_textview);
        sendButton = findViewById(R.id.send_button);
        connectButton = findViewById(R.id.connect_button);

        connectButton.setOnClickListener(v -> connectToServer());
        sendButton.setOnClickListener(v -> sendMessage());
    }

    private void connectToServer() {
        String ip = ipEditText.getText().toString();
        int port = Integer.parseInt(portEditText.getText().toString());

        clientThread = new Thread(() -> {
            try {
                socket = new Socket(ip, port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                handler.post(() -> chatboxTextView.append("\nConnected to server at " + ip + ":" + port));

                receiveThread = new Thread(new ReceiveMessages());
                receiveThread.start();

            } catch (Exception e) {
                handler.post(() -> chatboxTextView.append("\nConnection failed: " + e.getMessage()));
            }
        });
        clientThread.start();
    }

    private void sendMessage() {
        String message = messageEditText.getText().toString();
        if (out != null && !message.isEmpty()) {
            new Thread(() -> {
                out.println(message);
                handler.post(() -> {
                    chatboxTextView.append("\nYou: " + message);
                    messageEditText.setText("");
                });
            }).start();
        }
    }

    class ReceiveMessages implements Runnable {
        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    String finalMessage = message;
                    handler.post(() -> chatboxTextView.append("\nServer: " + finalMessage));
                }
            } catch (Exception e) {
                handler.post(() -> chatboxTextView.append("\nDisconnected from server"));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (clientThread != null && clientThread.isAlive()) {
            clientThread.interrupt();
        }
        if (receiveThread != null && receiveThread.isAlive()) {
            receiveThread.interrupt();
        }
        try {
            if (socket != null) socket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

