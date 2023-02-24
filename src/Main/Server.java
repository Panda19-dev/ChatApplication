package Main;

import Commands.KickCommand;
import Commands.NickCommand;
import Handlers.CommandHandler;
import Handlers.ConnectionHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    private ArrayList<ConnectionHandler> connections;

    private CommandHandler commandHandler;

    private ExecutorService pool;
    private ConnectionHandler connectionHandler;


    public Server() {
        this.connections = new ArrayList<>();
        this.commandHandler = new CommandHandler();
    }

    public void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            pool = Executors.newCachedThreadPool(); //Initializing pool
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket client = serverSocket.accept();
                System.out.println("New client connected");
                connectionHandler = new ConnectionHandler(client, commandHandler, this);
                connections.add(connectionHandler);
                pool.execute(connectionHandler);
                this.commandHandler.addCommand(new NickCommand(connectionHandler));
                this.commandHandler.addCommand(new KickCommand(this));
            }
        } catch (IOException e) {
            System.out.println("Server exception: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void removeHandler(ConnectionHandler handler) {
        connections.remove(handler);
    }

    public ArrayList<ConnectionHandler> getConnections() {
        return connections;
    }


    public void removeConnection(ConnectionHandler connection) {
        connections.remove(connection); // Remove the connection from the list of connections

        // Optionally, you could also send a message to the remaining clients to inform them that someone has left the chat
        String message = connection.getNickname() + " has left the chat.";
        connectionHandler.broadcast(message, "Server");
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.start(9999);
    }
}