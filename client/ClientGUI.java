package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.handlers.PrivateChatHandler;

public class ClientGUI {
    private static final Logger logger = Logger.getLogger(ClientGUI.class.getName());
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private volatile boolean done;

    private PrivateChatHandler privateChatHandler; // Instance of PrivateChatHandler
    private final String serverAddress;
    private final int port;

    public ClientGUI(String serverAddress, int port) {
        this.serverAddress = serverAddress;
        this.port = port;

        setupUI();
        connectToServer();

        // Shutdown hook to ensure cleanup
        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    private void connectToServer() {
        try {
            client = new Socket(serverAddress, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));
            privateChatHandler = new PrivateChatHandler(out); // Initialize the chat handler

            new Thread(new IncomingReader()).start();
            logger.info("Connected to the server at " + serverAddress + ":" + port);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Connection error: " + e.getMessage(), e);
            showErrorMessage("Unable to connect to server. Please try again later.");
            shutdown();
        }
    }

    // Send message to the server
    private void sendMessage() {
        String message = inputField.getText().trim();
        if (!message.isEmpty()) {
            out.println(message);
            inputField.setText(""); // Clear the input field
        }
    }

    // Graceful shutdown of the client
    private void shutdown() {
        done = true;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null && !client.isClosed()) client.close();
            logger.info("Client disconnected and resources closed.");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error during shutdown: " + e.getMessage(), e);
        }
    }

    // Thread to read incoming messages from the server
    private class IncomingReader implements Runnable {
        @Override
        public void run() {
            String message;
            try {
                while (!done && (message = in.readLine()) != null) {
                    processMessage(message);
                    logger.info("Message received: " + message);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Connection lost. Attempting to reconnect...", e);
                showErrorMessage("Connection lost. Trying to reconnect...");
                reconnect();
            }
        }
    }

    private void processMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            if (message.startsWith("PM from")) {
                handlePrivateMessage(message);
            } else {
                messageArea.append(message + "\n");
            }
        });
    }

    private void handlePrivateMessage(String message) {
        int senderEndIndex = message.indexOf(":");
        if (senderEndIndex != -1) {
            String sender = message.substring(8, senderEndIndex).trim(); // Extract sender name
            String privateMessage = message.substring(senderEndIndex + 1).trim(); // Extract message content
            privateChatHandler.displayMessage(sender, privateMessage);
        }
    }

    private void reconnect() {
        shutdown();
        try {
            Thread.sleep(3000); // Wait before attempting to reconnect
            connectToServer();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.log(Level.SEVERE, "Reconnection attempt interrupted.", e);
        }
    }

    private void showErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(frame, message, "Error", JOptionPane.ERROR_MESSAGE));
    }

    private void setupUI() {
        frame = new JFrame("Chat Client");
        messageArea = new JTextArea(20, 50);
        messageArea.setEditable(false);
        inputField = new JTextField(50);
        JButton sendButton = new JButton("Send");

        // Layout setup
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Action listeners
        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        // Frame settings
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1"; // Change if needed
        int port = 9999; // Change if needed
        SwingUtilities.invokeLater(() -> new ClientGUI(serverAddress, port));
    }
}