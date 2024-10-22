package server;

import models.commands.*;
import models.handlers.CommandHandler;
import models.handlers.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ArrayList<ConnectionHandler> connections;
    private final HashMap<String, ArrayList<ConnectionHandler>> groups; // Map to store groups and their members
    private final CommandHandler commandHandler;
    private ExecutorService pool;

    private ConnectionHandler connectionHandler;

    public Server() {
        this.connections = new ArrayList<>();
        groups = new HashMap<>();
        this.commandHandler = CommandHandler.getInstance();  // Use the singleton instance
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            pool = Executors.newCachedThreadPool();
            System.out.println("Server is listening on port " + port);

            // Register commands only once outside the loop
            commandHandler.addCommand(new PrivateMessageCommand(this));
            commandHandler.addCommand(new NickCommand());
            commandHandler.addCommand(new KickCommand(this));
            commandHandler.addCommand(new HelpCommand(commandHandler));
            commandHandler.addCommand(new QuitCommand());

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New client connected");
                connectionHandler = new ConnectionHandler(client, commandHandler, this);
                connections.add(connectionHandler);
                pool.execute(connectionHandler);
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public ConnectionHandler findHandlerByNickname(String nickname) {
        for (ConnectionHandler handler : connections) {
            if (handler.getNickname().equals(nickname)) {
                return handler;
            }
        }
        return null; // Not found
    }

    public void removeHandler(ConnectionHandler handler) {
        connections.remove(handler);
    } // Removes a connectionHandler

    public ArrayList<ConnectionHandler> getConnections() {
        return connections;
    } // returns the list of connectionhandlers.


    public void removeConnection(ConnectionHandler connection) { //Removes a connection (player) from the server.
        connections.remove(connection); // Remove the connection from the list of connections

        // Optionally, you could also send a message to the remaining clients to inform them that someone has left the chat
        String message = connection.getNickname() + " has left the chat.";
        connectionHandler.broadcastAsServer(message);
    }

    public static void main(String[] args) { // Start method that is started by the green arrow.
        Server server = new Server();
        server.start(9999);
    }

}

