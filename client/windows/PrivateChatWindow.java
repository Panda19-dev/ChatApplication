package client.windows;

import models.handlers.PrivateChatHandler;

import javax.swing.*;
import java.awt.*;
import java.io.PrintWriter;

public class PrivateChatWindow {
    private JFrame frame;
    private JTextArea messageArea;
    private JTextField inputField;
    private PrintWriter out;
    private String partner;
    private PrivateChatHandler chatHandler;

    public PrivateChatWindow(String partner, PrintWriter out, PrivateChatHandler chatHandler) {
        this.partner = partner;
        this.out = out;
        this.chatHandler = chatHandler;

        // Register this chat window with the handler
        chatHandler.registerChatWindow(this);

        // Initialize components
        frame = new JFrame("Private Chat with " + partner);
        messageArea = new JTextArea(20, 30);
        inputField = new JTextField(30);
        JButton sendButton = new JButton("Send");

        // Set up the message area
        messageArea.setEditable(false);
        messageArea.setLineWrap(true); // Wrap long lines
        messageArea.setWrapStyleWord(true);
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

        // Add window listener
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                closeWindow(); // Call your method to unregister and dispose
            }
        });

        // Finalize the frame setup
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void sendMessage() {
        String message = inputField.getText();
        if (!message.isBlank()) {
            out.println("/pm " + partner + " " + message);
            displayMessage("You: " + message);
            inputField.setText("");
            messageArea.setCaretPosition(messageArea.getDocument().getLength()); // Scroll to the bottom
        }
    }

    public void displayMessage(String message) {
        messageArea.append(message + "\n");
        messageArea.setCaretPosition(messageArea.getDocument().getLength()); // Scroll to the bottom
    }

    public String getPartner() {
        return partner;
    }

    public void closeWindow() {
        chatHandler.unregisterChatWindow(this);
        frame.dispose();
    }
}