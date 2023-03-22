package Handlers;

import Exceptions.CommandNotFoundException;
import Exceptions.InvalidCommandArgumentException;
import Main.Server;

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

    public ConnectionHandler(Socket client, CommandHandler commandHandler, Server server) throws IOException { // Constructor
        this.client = client;
        this.commandHandler = commandHandler;
        this.server = server;
        out = new PrintWriter(client.getOutputStream(), true); //Initializing writer
        in = new BufferedReader(new InputStreamReader(client.getInputStream())); //Initializing reader
    }

    @Override
    public void run() { // Method that is called when the connectionhandler class is runned.
        try {
            String prefix = "/"; // THE PREFIX FOR ALL COMMANDS
            out.println("Please enter a nickname: ");
            nickname = in.readLine();
            System.out.println(nickname + " connected!");
            broadcast(nickname + " joined the chat!", nickname);
            String message;
            while ((message = in.readLine()) != null && !message.isEmpty() && !message.isBlank()) {
                if (message.startsWith(prefix)) {
                    String[] messageSplit = message.split(" ");
                    String commandName = messageSplit[0].substring(prefix.length());
                    String[] commandArgs = new String[messageSplit.length - 1];
                    System.arraycopy(messageSplit, 1, commandArgs, 0, commandArgs.length);
                    try {
                        commandHandler.executeCommand(commandName, commandArgs);
                    } catch (CommandNotFoundException | InvalidCommandArgumentException e) {
                        sendMessage("SERVER: " + e.getMessage());
                    }
                } else {
                    broadcast(nickname + ": " + message, nickname);
                }
            }
        } catch (IOException e) {
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

    public void broadcast(String message, String sender) { // Broadcasts a message to all clients connected except the sender.
        for (ConnectionHandler ch : server.getConnections()) {
            if (!ch.equals(this) && ch.getNickname() != null) {
                ch.sendMessage(sender + ": " + message);
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


    public void shutdown() { // Method that is called when a client is disconnecting from the server
        try {
            in.close();
            out.close();
            if (!client.isClosed()) {
                client.close();
            }
            server.removeHandler(this);
            broadcast(nickname + " left the chat!", nickname);
            System.out.println(nickname + " disconnected!");
        } catch (IOException e) {
            //ignore
        }
    }
}