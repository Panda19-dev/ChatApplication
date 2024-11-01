package client;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import client.windows.PrivateChatWindow;

public class ClientGUI {
    private static final Logger logger = Logger.getLogger(ClientGUI.class.getName());
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private volatile boolean done;

    // Map to keep track of private chat windows by username
    private final Map<String, PrivateChatWindow> privateChats = new HashMap<>();

    public ClientGUI(String serverAddress, int port) {
        setupUI();

        // Connect to the server
        try {
            client = new Socket(serverAddress, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            new Thread(new IncomingReader()).start();
            logger.info("Connected to the server at " + serverAddress + ":" + port);
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Connection error: " + e.getMessage(), e);
            shutdown();
        }
    }

    // Send message to the server
    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isBlank()) {
            out.println(message);
            inputField.setText(""); // Clear the input field
        }
    }

    // Close resources
    private void shutdown() {
        done = true;
        try {
            if (in != null) in.close();
            if (out != null) out.close();
            if (client != null && !client.isClosed()) client.close();
            logger.info("Client resources closed successfully.");
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
                    if (message.startsWith("PM from")) {
                        int senderEndIndex = message.indexOf(":");
                        if (senderEndIndex != -1) {
                            String sender = message.substring(8, senderEndIndex).trim(); // Extract sender name
                            String privateMessage = message.substring(senderEndIndex + 1).trim(); // Extract message content
                            openPrivateChatWindow(sender, privateMessage); // Call the method to open the chat window
                        }
                    } else {
                        messageArea.append(message + "\n");
                    }
                    logger.info("Message received: " + message);
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error reading from server: " + e.getMessage(), e);
                shutdown();
            }
        }
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

    // Open or update a private chat window with a given user
    private void openPrivateChatWindow(String username, String message) {
        SwingUtilities.invokeLater(() -> {
            String trimmedUsername = username.trim();
            String trimmedMessage = message.trim();

            System.out.println("Opening private chat with: " + trimmedUsername);
            PrivateChatWindow chatWindow = privateChats.get(trimmedUsername);
            if (chatWindow == null) {
                System.out.println("Creating new chat window for: " + trimmedUsername);
                chatWindow = new PrivateChatWindow(trimmedUsername, out);
                privateChats.put(trimmedUsername, chatWindow);
            }
            chatWindow.displayMessage("PM from " + trimmedUsername + ": " + trimmedMessage);
        });
    }

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1"; // Change if needed
        int port = 9999; // Change if needed
        SwingUtilities.invokeLater(() -> new ClientGUI(serverAddress, port));
    }
}