package client.windows;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PrivateChatWindow {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private PrintWriter out;
    private String partner;

    public PrivateChatWindow(String partner, PrintWriter out) {
        this.partner = partner;
        this.out = out;

        frame = new JFrame("Private Chat with " + partner);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Allow window to be closed
        messageArea = new JTextArea(20, 30);
        messageArea.setEditable(false);

        // Layout setup
        frame.setLayout(new BorderLayout());
        frame.add(new JScrollPane(messageArea), BorderLayout.CENTER);

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(inputField, BorderLayout.CENTER);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(e -> sendMessage());
        inputField.addActionListener(e -> sendMessage());
        inputPanel.add(sendButton, BorderLayout.EAST);

        frame.add(inputPanel, BorderLayout.SOUTH);

        frame.pack();
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isBlank()) {
            String timestampedMessage = "[" + new SimpleDateFormat("HH:mm:ss").format(new Date()) + "] You: " + message;
            out.println("/pm " + partner + " " + message);
            displayMessage(timestampedMessage);
            inputField.setText("");
        }
    }

    public void displayMessage(String message) {
        messageArea.append(message + "\n");
    }
}
