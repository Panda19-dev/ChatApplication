import Commands.NickCommand;
import Handlers.CommandHandler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server implements Runnable {

    private CommandHandler commandHandler;
    private ArrayList<ConnectionHandler> connections;
    private ServerSocket server; //A server socket waits for requests to come in over the network. It performs some operation based on that request, and then possibly returns a result to the requester.
    private boolean done;
    private ExecutorService pool;

    public Server() { //Class constructor
        connections = new ArrayList<>(); //Initializing the arraylist
        done = false;
        this.commandHandler = CommandHandler.getInstance();
        this.commandHandler.addCommand(new NickCommand());
    }

    @Override
    public void run() { //Method that runs when application starts.

        try {
            server = new ServerSocket(9999); //Initializing server
            pool = Executors.newCachedThreadPool(); //Initializing pool
            while (!done) {
                Socket client = server.accept(); //Listens for a connection to be made to this socket and accepts it. The method blocks until a connection is made.
                ConnectionHandler handler = new ConnectionHandler(client);
                connections.add(handler); //Appends the specified element to the end of this list.
                pool.execute(handler);
            }
        } catch (IOException e) {
            shutdown();
        }

    }

    public void broadcast(String message, String nickname) { //Send message to all people in the ConnectionHandler
        for (ConnectionHandler ch : connections) {
            if (ch != null) {
                if (ch.nickname.equals(nickname)) { //Do not send message to message owner twice.(NEEDS TO BE REMOVED WHEN GRAPHICS IS DONE)
                    continue;
                }
                //Send the message if it isn´t the message owner.
                ch.sendMessage(message);
            }
        }
    }
    public void shutdown() { //Close and shutdown
        try {
            done = true;
            pool.shutdown();
            if (!server.isClosed()) {
                server.close();
            }
            for (ConnectionHandler ch : connections) {
                ch.shutdown();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    class ConnectionHandler implements Runnable { //Inner Class that represent all people that connects via a client.

        private Socket client;
        private BufferedReader in;
        private PrintWriter out;
        private String nickname;

        public ConnectionHandler(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                String prefix = "/"; // THE PREFIX FOR ALL COMMANDS
                out = new PrintWriter(client.getOutputStream(), true); //Initializing writer
                in = new BufferedReader(new InputStreamReader(client.getInputStream())); //Initializing reader
                out.println("Please enter a nickname: ");
                nickname = in.readLine();
                System.out.println(nickname + " connected!");
                broadcast(nickname + " joined the chat!", nickname);
                String message;
                while ((message = in.readLine()) != null) {
                    if (message.startsWith(prefix + "nick")) { //NICK COMMAND
                        String[] messageSplit = message.split(" ", 2);
                        if (messageSplit.length == 2) {
                            broadcast(nickname + " renamed themselves to " + messageSplit[1], nickname);
                            System.out.println(nickname + " changed nickname to " + messageSplit[1]);
                            nickname = messageSplit[1];
                            out.println("Successfully changed nickname to " + nickname);
                        } else {
                            out.println("No nickname provided!");
                        }
                    } else if (message.startsWith(prefix + "quit")) {
                        shutdown();
                        broadcast(nickname + " left the chat!", nickname);
                        System.out.println(nickname + " disconnected!");
                    } else if (message.startsWith(prefix + "kick")) { //KICK COMMAND
                        String[] messageSplit = message.split(" ", 2);
                        if(messageSplit.length == 2) {
                            try {
                                for (ConnectionHandler ch : connections) {
                                    if(messageSplit[1].trim().equalsIgnoreCase(ch.nickname)) {
                                        ch.sendMessage("You have been kicked!");
                                        ch.shutdown();
                                        broadcast("SERVER: " + ch.nickname + " has been kicked", "SERVER");
                                        System.out.println("SERVER: " + ch.nickname + " has been kicked");
                                    }
                                }
                            } catch(Exception e) {
                                System.out.println(e.getMessage());
                            }
                        } else {
                            sendMessage("SERVER: You need to specify the member you want to kick.");
                        }
                    } else {
                            broadcast(nickname + ": " + message, nickname);
                        }
                    }
            } catch (IOException e) {
                shutdown();
            }
        }
        public void sendMessage(String message) {
            out.println(message);
        }
        public void shutdown() {
            try {
                in.close();
                out.close();
                if (!client.isClosed()) {
                    client.close();
                }
            } catch (IOException e) {
                //ignore
            }
        }
    }

    public static void main(String[] args) {
        Server server = new Server();
        server.run();
    }
}
