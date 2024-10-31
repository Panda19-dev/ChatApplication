package client;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientGUI {
    private static final Logger logger = Logger.getLogger(ClientGUI.class.getName());
    private Socket client;
    private BufferedReader in;
    private PrintWriter out;
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private volatile boolean done;

    public ClientGUI(String serverAddress, int port) {
        frame = new JFrame("Chat Client");
        messageArea = new JTextArea(20, 50);
        inputField = new JTextField(50);
        JButton sendButton = new JButton("Send");

        // Set up the GUI layout
        messageArea.setEditable(false);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Add action listeners for sending messages
        inputField.addActionListener(e -> sendMessage());
        sendButton.addActionListener(e -> sendMessage());

        // Set up the frame
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        // Connect to the server
        try {
            client = new Socket(serverAddress, port);
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            new Thread(new IncomingReader()).start();
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
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null && !client.isClosed()) {
                client.close();
            }
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
                    messageArea.append(message + "\n");
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error reading from server: " + e.getMessage(), e);
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        String serverAddress = "127.0.0.1"; // Change if needed
        int port = 9999; // Change if needed
        SwingUtilities.invokeLater(() -> new ClientGUI(serverAddress, port));
    }
}