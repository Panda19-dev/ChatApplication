package server;

import models.commands.*;
import models.commands.groups.CreateGroupCommand;
import models.commands.groups.JoinGroupCommand;
import models.commands.groups.LeaveGroupCommand;
import models.handlers.CommandHandler;
import models.handlers.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private final ArrayList<ConnectionHandler> connections;
    private final CommandHandler commandHandler;
    private final Map<String, List<ConnectionHandler>> groups; // Store groups and their members

    private ExecutorService pool;

    public Server() {
        this.connections = new ArrayList<>();
        this.commandHandler = CommandHandler.getInstance();  // Use the singleton instance
        this.groups = new HashMap<>();
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
            commandHandler.addCommand(new CreateGroupCommand(this)); // Add CreateGroupCommand
            commandHandler.addCommand(new JoinGroupCommand(this)); // Add JoinGroupCommand
            commandHandler.addCommand(new LeaveGroupCommand(this)); // Add LeaveGroupCommand

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New client connected");
                ConnectionHandler connectionHandler = new ConnectionHandler(client, commandHandler, this);
                connections.add(connectionHandler);
                pool.execute(connectionHandler);
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Method to create a group
    public synchronized void createGroup(String groupName, ConnectionHandler creator) {
        if (groups.containsKey(groupName)) {
            creator.sendMessage("Server: Group '" + groupName + "' already exists.");
        } else {
            List<ConnectionHandler> members = new ArrayList<>();
            members.add(creator);
            groups.put(groupName, members);
            creator.sendMessage("Server: Group '" + groupName + "' created and you have joined.");
        }
    }

    // Method for a client to join a group
    public synchronized void joinGroup(String groupName, ConnectionHandler handler) {
        List<ConnectionHandler> members = groups.get(groupName);

        if (members == null) {
            // Inform the user that the group does not exist
            handler.sendMessage("Server: Group '" + groupName + "' does not exist.");
            return; // Exit the method since the group doesn't exist
        }

        // Check if the handler is already a member of the group
        if (!members.contains(handler)) {
            members.add(handler); // Add the handler to the group
            handler.sendMessage("Server: You joined the group '" + groupName + "'.");
            broadcastToGroup(groupName, "Server: " + handler.getNickname() + " has joined the group.");
        } else {
            handler.sendMessage("Server: You are already a member of '" + groupName + "'.");
        }
    }

    // Method for a client to leave a group
    public synchronized void leaveGroup(String groupName, ConnectionHandler handler) {
        List<ConnectionHandler> members = groups.get(groupName);
        if (members == null) {
            handler.sendMessage("Server: Group '" + groupName + "' does not exist.");
        } else if (!members.contains(handler)) {
            handler.sendMessage("Server: You are not a member of '" + groupName + "'.");
        } else {
            members.remove(handler);
            handler.sendMessage("Server: You have left the group '" + groupName + "'.");
            broadcastToGroup(groupName, "Server: " + handler.getNickname() + " has left the group.");
            if (members.isEmpty()) {
                groups.remove(groupName);
                System.out.println("Server: Group '" + groupName + "' has been removed as it has no members.");
            }
        }
    }

    // Method to broadcast a message to a specific group
    public void broadcastToGroup(String groupName, String message) {
        List<ConnectionHandler> members = groups.get(groupName);
        if (members != null) {
            for (ConnectionHandler member : members) {
                member.sendMessage(message);
            }
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
    }

    public ArrayList<ConnectionHandler> getConnections() {
        return connections;
    }

    public void removeConnection(ConnectionHandler connection) {
        connections.remove(connection);
        String message = connection.getNickname() + " has left the chat.";
        connection.broadcastAsServer(message);
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(9999);
    }
}