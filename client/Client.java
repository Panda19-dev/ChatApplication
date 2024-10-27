package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Client implements Runnable {

    private static final Logger logger = Logger.getLogger(Client.class.getName());
    private Socket client; // A socket is an endpoint for communication between two machines.
    private BufferedReader in; // Reads text from a character-input stream.
    private PrintWriter out; // Prints formatted representations of objects to a text-output stream.
    private volatile boolean done; // Use volatile to ensure visibility across threads.

    @Override
    public void run() { // Method that is called when client class is starting.
        try {
            client = new Socket("127.0.0.1", 9999); // Creates a stream socket and connects it to the specified port number on the named host.
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread inputThread = new Thread(inHandler);
            inputThread.start();

            String inMessage;
            while ((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Connection error: " + e.getMessage(), e);
            shutdown();
        } finally {
            shutdown(); // Ensure shutdown is called on exit
        }
    }

    public void shutdown() { // Method that is called when client is disconnecting.
        done = true;
        try {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
            if (client != null && !client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Error during shutdown: " + e.getMessage(), e);
        }
    }

    class InputHandler implements Runnable { // Handles input (messages) from a client.

        @Override
        public void run() { // Method that is called when a client is sending a message.
            try (BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in))) {
                while (!done) {
                    String message = inReader.readLine();
                    if (message != null && !message.trim().isEmpty()) {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                logger.log(Level.SEVERE, "Error reading input: " + e.getMessage(), e);
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        new Thread(client).start(); // Start the client on a new thread
    }
}
