package models.handlers;

import server.Server;
import utils.exceptions.CommandNotFoundException;
import utils.exceptions.InvalidCommandArgumentException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConnectionHandler implements Runnable {
    private static final Logger logger = Logger.getLogger(ConnectionHandler.class.getName());
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;
    private String nickname;
    private final CommandHandler commandHandler;
    private volatile boolean kicked;
    private final Server server;
    private volatile boolean running;

    public ConnectionHandler(Socket client, CommandHandler commandHandler, Server server) throws IOException {
        this.client = client;
        this.commandHandler = commandHandler;
        this.server = server;
        this.out = new PrintWriter(client.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(client.getInputStream()));
        this.running = true; // Thread should be running initially
        this.kicked = false;
    }

    @Override
    public void run() {
        try {
            // Welcome the client and request a nickname
            out.println("Please enter a nickname: ");
            nickname = in.readLine();

            // Ensure nickname is valid
            if (nickname == null || nickname.isBlank()) {
                throw new InvalidCommandArgumentException("Invalid nickname. Please provide a valid nickname.");
            }

            logger.info(nickname + " connected!");
            broadcastAsServer(nickname + " joined the chat!");

            // Main loop: keep listening for incoming messages
            String message;
            while (running && (message = in.readLine()) != null) {
                if (!message.isBlank()) {
                    processMessage(message);
                }
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Connection error: " + e.getMessage(), e);
            shutdown();
        } catch (InvalidCommandArgumentException e) {
            sendMessage("SERVER: " + e.getMessage());
            shutdown();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error: " + e.getMessage(), e);
            shutdown();
        }
    }

    private void processMessage(String message) {
        String prefix = "/";
        if (message.startsWith(prefix)) {
            // Command message
            String[] messageSplit = message.split(" ");
            String commandName = messageSplit[0].substring(prefix.length());
            String[] commandArgs = new String[messageSplit.length - 1];
            System.arraycopy(messageSplit, 1, commandArgs, 0, commandArgs.length);

            try {
                commandHandler.executeCommand(commandName, commandArgs, this);
            } catch (CommandNotFoundException | InvalidCommandArgumentException e) {
                sendMessage("SERVER: " + e.getMessage());
            }
        } else {
            // Regular chat message
            broadcast(message, nickname);
        }
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void sendMessage(String message) {
        if (!kicked) {
            out.println(message);
        }
    }

    public void broadcast(String message, String sender) {
        for (ConnectionHandler ch : server.getConnections()) {
            if (ch.getNickname() != null && !ch.getNickname().isBlank()) {
                ch.sendMessage(sender + ": " + message);
            }
        }
    }

    public void broadcastAsServer(String message) {
        for (ConnectionHandler ch : server.getConnections()) {
            if (ch.getNickname() != null && !ch.getNickname().isBlank()) {
                ch.sendMessage("Server: " + message);
            }
        }
    }

    public void kick() {
        kicked = true;
        try {
            if (client != null && !client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error closing client socket: " + e.getMessage(), e);
        }
    }

    public void shutdown() {
        running = false;
        try {
            // Close resources and handle cleanup
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null && !client.isClosed()) {
                client.close();
            }
            server.removeHandler(this);
            broadcastAsServer(nickname + " left the chat.");
            logger.info(nickname + " disconnected!");
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error during shutdown: " + e.getMessage(), e);
        }
    }
}