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
import java.net.ServerSocket;
import java.net.Socket;

public class ServerActivity extends AppCompatActivity {

    private TextView serverStatusTextView, chatboxTextView;
    private EditText messageEditText;
    private Button sendButton;
    private Handler handler = new Handler();
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private PrintWriter out;
    private BufferedReader in;
    private Thread serverThread, clientThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_server);

        serverStatusTextView = findViewById(R.id.server_status_textview);
        chatboxTextView = findViewById(R.id.server_chatbox_textview);
        messageEditText = findViewById(R.id.server_message_edit_text);
        sendButton = findViewById(R.id.server_send_button);

        sendButton.setOnClickListener(view -> sendMessage());

        serverThread = new Thread(new ServerRunnable());
        serverThread.start();
    }

    class ServerRunnable implements Runnable {
        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(12345);
                handler.post(() -> serverStatusTextView.setText("Server started on port 12345, waiting for connection..."));

                while (true) {
                    clientSocket = serverSocket.accept();
                    handler.post(() -> {
                        serverStatusTextView.setText("Client connected");
                        chatboxTextView.append("\nClient connected!");
                    });

                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

                    clientThread = new Thread(new ClientHandler(clientSocket, in, out));
                    clientThread.start();
                }
            } catch (Exception e) {
                handler.post(() -> serverStatusTextView.setText("Server error!"));
                e.printStackTrace();
            }
        }
    }

    class ClientHandler implements Runnable {
        private final Socket clientSocket;
        private final BufferedReader in;
        private final PrintWriter out;

        ClientHandler(Socket socket, BufferedReader in, PrintWriter out) {
            this.clientSocket = socket;
            this.in = in;
            this.out = out;
        }

        @Override
        public void run() {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    String finalMessage = message;
                    handler.post(() -> chatboxTextView.append("\nClient: " + finalMessage));

                    if (finalMessage.equalsIgnoreCase("exit")) {
                        break;
                    }
                }
                clientSocket.close();
                handler.post(() -> chatboxTextView.append("\nClient disconnected"));
            } catch (Exception e) {
                handler.post(() -> chatboxTextView.append("\nClient error"));
                e.printStackTrace();
            }
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (serverThread != null && serverThread.isAlive()) {
            serverThread.interrupt();
        }
        if (clientThread != null && clientThread.isAlive()) {
            clientThread.interrupt();
        }
        try {
            if (serverSocket != null) serverSocket.close();
            if (clientSocket != null) clientSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
