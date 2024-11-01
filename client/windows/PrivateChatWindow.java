package client.windows;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class PrivateChatWindow {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private PrintWriter out;
    private String partner;

    public PrivateChatWindow(String partner, PrintWriter out) {
        this.partner = partner;
        this.out = out;

        // Initialize components
        frame = new JFrame("Private Chat with " + partner);
        messageArea = new JTextArea(20, 30);
        inputField = new JTextField(30);
        JButton sendButton = new JButton("Send");

        // Set up the message area
        messageArea.setEditable(false);
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        // Set up the input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);
        frame.add(inputPanel, BorderLayout.SOUTH);

        // Add action listeners
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());

        // Finalize the frame setup
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Close only this window
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isBlank()) {
            out.println("/pm " + partner + " " + message);
            displayMessage("You: " + message);
            inputField.setText("");
        }
    }

    public void displayMessage(String message) {
        messageArea.append(message + "\n");
    }
}
