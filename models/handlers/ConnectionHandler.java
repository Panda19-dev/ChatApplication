package models.handlers;

import server.Server;
import utils.exceptions.CommandNotFoundException;
import utils.exceptions.InvalidCommandArgumentException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ConnectionHandler implements Runnable {
    private final Socket client;
    private final BufferedReader in;
    private final PrintWriter out;
    private String nickname;
    private final CommandHandler commandHandler;
    private boolean kicked;
    private final Server server;
    private boolean running; // Add this flag

    public ConnectionHandler(Socket client, CommandHandler commandHandler, Server server) throws IOException { // Constructor
        this.client = client;
        this.commandHandler = commandHandler;
        this.server = server;
        out = new PrintWriter(client.getOutputStream(), true); //Initializing writer
        in = new BufferedReader(new InputStreamReader(client.getInputStream())); //Initializing reader
        this.running = true; // Set the flag to true initially
    }

    // Adjust the exception handling in the run() method
    @Override
    public void run() {
        try {
            String prefix = "/";
            out.println("Please enter a nickname: ");
            nickname = in.readLine();

            if (nickname == null || nickname.isBlank()) {
                throw new InvalidCommandArgumentException("Invalid nickname. Please provide a valid nickname.");
            }

            System.out.println(nickname + " connected!");
            broadcastAsServer(nickname + " joined the chat!");

            String message;
            while (running && (message = in.readLine()) != null) {
                if (!message.isBlank()) {
                    if (message.startsWith(prefix)) {
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
                        broadcast(message, nickname);
                    }
                }
            }
        } catch (IOException e) {
            shutdown();
        } catch (InvalidCommandArgumentException e) {
            sendMessage(e.getMessage());
            shutdown();
        }
    }

    public String getNickname() {
        return nickname;
    } // Get the nickname

    public void setNickname(String nickname) {
        this.nickname = nickname;
    } //Changes nickname

    public void sendMessage(String message) { //Sends a message
        if(!kicked) {
            out.println(message);
        }
    }

    // Broadcast with a specific sender (used for regular chat messages)
    public void broadcast(String message, String sender) {
        for (ConnectionHandler ch : server.getConnections()) {
            if (ch.getNickname() != null && !ch.getNickname().isBlank()) {
                ch.sendMessage(sender + ": " + message);
            }
        }
    }

    // Broadcast as the server (used for system messages like kicks or disconnects)
    public void broadcastAsServer(String message) {
        for (ConnectionHandler ch : server.getConnections()) {
            if (ch.getNickname() != null && !ch.getNickname().isBlank()) {
                ch.sendMessage("Server: " + message);
            }
        }
    }

    public void kick() { // Closes the client
        kicked = true;
        try {
            client.close();
        } catch (IOException e) {
            // ignore
        }
    }

    public void shutdown() {
        running = false; // Stop the loop
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
            server.removeHandler(this);
            broadcastAsServer(nickname + " left the chat.");
            System.out.println(nickname + " disconnected!");
        } catch (IOException e) {
            // ignore
        }
    }
}
