package models.handlers;

import client.windows.PrivateChatWindow;

import java.util.HashMap;
import java.util.Map;
import java.io.PrintWriter;

public class PrivateChatHandler {
    private final Map<String, PrivateChatWindow> chatWindows;
    private final PrintWriter out;

    public PrivateChatHandler(PrintWriter out) {
        this.chatWindows = new HashMap<>();
        this.out = out;
    }

    // Method to get or create a PrivateChatWindow for a specific partner
    public PrivateChatWindow getOrCreateChatWindow(String partner) {
        return chatWindows.computeIfAbsent(partner, p -> new PrivateChatWindow(p, out, this));
    }

    // Registers a new chat window for a partner
    public void registerChatWindow(PrivateChatWindow chatWindow) {
        chatWindows.put(chatWindow.getPartner(), chatWindow);
    }

    // Unregisters a chat window when it's closed
    public void unregisterChatWindow(PrivateChatWindow chatWindow) {
        chatWindows.remove(chatWindow.getPartner());
    }

    // Displays a message in the appropriate chat window
    public void displayMessage(String partner, String message) {
        PrivateChatWindow chatWindow = chatWindows.get(partner);
        if (chatWindow != null) {
            chatWindow.displayMessage(message);
        } else {
            // Create a new chat window if it doesn't exist and display the message
            chatWindow = new PrivateChatWindow(partner, out, this);
            chatWindows.put(partner, chatWindow);
            chatWindow.displayMessage(message);
        }
    }

    // Optional: Method to close all chat windows (could be used on logout or shutdown)
    public void closeAllChatWindows() {
        for (PrivateChatWindow window : chatWindows.values()) {
            window.closeWindow();
        }
        chatWindows.clear();
    }
}