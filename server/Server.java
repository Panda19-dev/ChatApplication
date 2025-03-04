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
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class Server {

    private static final Logger logger = Logger.getLogger(Server.class.getName());
    private final CopyOnWriteArrayList<ConnectionHandler> connections; // Thread-safe list
    private final CommandHandler commandHandler;
    private final ConcurrentHashMap<String, List<ConnectionHandler>> groups; // Thread-safe group storage
    private ExecutorService pool;

    public Server() {
        this.connections = new CopyOnWriteArrayList<>();
        this.commandHandler = CommandHandler.getInstance();  // Singleton
        this.groups = new ConcurrentHashMap<>();
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            pool = Executors.newCachedThreadPool();
            logger.info("Server is listening on port " + port);

            // Register commands
            registerCommands();

            while (true) {
                try {
                    Socket client = serverSocket.accept();
                    logger.info("New client connected: " + client.getInetAddress());
                    ConnectionHandler connectionHandler = new ConnectionHandler(client, commandHandler, this);
                    connections.add(connectionHandler);
                    pool.execute(connectionHandler);
                } catch (IOException e) {
                    logger.severe("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.severe("Server exception: " + e.getMessage());
        } finally {
            shutdown();
        }
    }

    private void registerCommands() {
        commandHandler.addCommand(new PrivateMessageCommand(this));
        commandHandler.addCommand(new NickCommand());
        commandHandler.addCommand(new KickCommand(this));
        commandHandler.addCommand(new HelpCommand(commandHandler));
        commandHandler.addCommand(new QuitCommand());
        commandHandler.addCommand(new CreateGroupCommand(this));
        commandHandler.addCommand(new JoinGroupCommand(this));
        commandHandler.addCommand(new LeaveGroupCommand(this));
    }

    public synchronized boolean createGroup(String groupName, ConnectionHandler creator) {
        if (!isValidGroupName(groupName)) {
            creator.sendMessage("Server: Invalid group name.");
            return false;
        }

        if (groups.containsKey(groupName)) {
            creator.sendMessage("Server: Group '" + groupName + "' already exists.");
            return false;
        }

        groups.put(groupName, new CopyOnWriteArrayList<>(List.of(creator)));
        creator.sendMessage("Server: Group '" + groupName + "' created and you have joined.");
        return true;
    }

    public synchronized boolean joinGroup(String groupName, ConnectionHandler handler) {
        List<ConnectionHandler> members = groups.get(groupName);
        if (members == null) {
            handler.sendMessage("Server: Group '" + groupName + "' does not exist.");
            return false;
        }

        if (members.contains(handler)) {
            handler.sendMessage("Server: You are already a member of '" + groupName + "'.");
            return false;
        }

        members.add(handler);
        handler.sendMessage("Server: You joined the group '" + groupName + "'.");
        broadcastToGroup(groupName, "Server: " + handler.getNickname() + " has joined the group.");
        return true;
    }

    public synchronized boolean leaveGroup(String groupName, ConnectionHandler handler) {
        List<ConnectionHandler> members = groups.get(groupName);
        if (members == null || !members.contains(handler)) {
            handler.sendMessage("Server: You are not a member of '" + groupName + "'.");
            return false;
        }

        members.remove(handler);
        handler.sendMessage("Server: You have left the group '" + groupName + "'.");
        broadcastToGroup(groupName, "Server: " + handler.getNickname() + " has left the group.");

        if (members.isEmpty()) {
            groups.remove(groupName);
            logger.info("Server: Group '" + groupName + "' removed due to no members.");
        }
        return true;
    }

    public synchronized List<ConnectionHandler> getGroupMembers(String groupName) {
        return groups.get(groupName);
    }

    public void broadcastToGroup(String groupName, String message) {
        List<ConnectionHandler> members = groups.get(groupName);
        if (members != null) {
            for (ConnectionHandler member : members) {
                member.sendMessage(message);
            }
        }
    }

    public ConnectionHandler findHandlerByNickname(String nickname) {
        return connections.stream()
                .filter(handler -> handler.getNickname().equals(nickname))
                .findFirst()
                .orElse(null);
    }

    public void removeHandler(ConnectionHandler handler) {
        connections.remove(handler);
    }

    public List<ConnectionHandler> getConnections() {
        return List.copyOf(connections); // Returns an unmodifiable list to prevent external modifications
    }

    public void removeConnection(ConnectionHandler connection) {
        connections.remove(connection);
        String message = "Server: " + connection.getNickname() + " has left the chat.";
        broadcastAsServer(message);
    }

    private boolean isValidGroupName(String groupName) {
        return groupName != null && groupName.matches("[a-zA-Z0-9_]+");
    }

    private void broadcastAsServer(String message) {
        for (ConnectionHandler connection : connections) {
            connection.sendMessage(message);
        }
    }

    private void shutdown() {
        logger.info("Shutting down server...");
        try {
            pool.shutdownNow();
            for (ConnectionHandler connection : connections) {
                connection.shutdown();
            }
            connections.clear();
            groups.clear();
        } catch (Exception e) {
            logger.severe("Error during server shutdown: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 9999;
        Server server = new Server();
        server.start(port);
    }
}