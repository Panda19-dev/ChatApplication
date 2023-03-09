package Main;

import Commands.KickCommand;
import Commands.NickCommand;
import Handlers.CommandHandler;
import Handlers.ConnectionHandler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.module.Configuration;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    JDA jda = JDABuilder.createDefault("Nzg5NDg4NTY4NDgwOTU2NDE2.Gi2-_N.5M_7_iRtmT7lq1dJdo51ccOF99m-7TOggxrmNo").build();
    private ArrayList<ConnectionHandler> connections;

    private CommandHandler commandHandler;

    private ExecutorService pool;
    private ConnectionHandler connectionHandler;
    private File config;

    public Server() {
        this.connections = new ArrayList<>();
        this.commandHandler = new CommandHandler();
    }

    public void start(int port) {
        /*
        try {
            config = new File("config.yml");
            if (config.createNewFile()) {
                System.out.println("File created: " + config.getName());
            } else {
                System.out.println("File already exists.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the server config.");
            e.printStackTrace();
        }
        try {
            FileWriter myWriter = new FileWriter("config.yml", true);
            myWriter.write("");
            myWriter.close();
            System.out.println("Successfully saved server config.");
        } catch (IOException e) {
            System.out.println("An error occurred with the server config.");
            e.printStackTrace();
        }

        try {
            Scanner myReader = new Scanner(config);
            while (myReader.hasNextLine()) {
                String data = myReader.nextLine();
                System.out.println(data);
            }
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred with the config.");
            e.printStackTrace();
        }

         */

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
        JDABuilder builder = JDABuilder.createDefault("Nzg5NDg4NTY4NDgwOTU2NDE2.Gi2-_N.5M_7_iRtmT7lq1dJdo51ccOF99m-7TOggxrmNo");

        // Disable parts of the cache
        builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
        // Enable the bulk delete event
        builder.setBulkDeleteSplittingEnabled(false);
        // Set activity (like "playing Something")
        builder.setActivity(Activity.playing("your mom"));

        builder.build();

        Server server = new Server();
        server.start(9999);
    }
}