package Main;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Client implements Runnable {

    private Socket client; //A socket is an endpoint for communication between two machines.
    private BufferedReader in;  //Reads text from a character-input stream, buffering characters to provide for the efficient reading of characters, arrays, and lines.
    private PrintWriter out; //Prints formatted representations of objects to a text-output stream.
    private boolean done;

    @Override
    public void run() { // method that is called when client class is starting.
        try {
            client = new Socket("127.0.0.1", 9999); //Creates a stream socket and connects it to the specified port number on the named host.
            out = new PrintWriter(client.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(client.getInputStream()));

            InputHandler inHandler = new InputHandler();
            Thread t = new Thread(inHandler);
            t.start();

            String inMessage;
            while((inMessage = in.readLine()) != null) {
                System.out.println(inMessage);
            }
        } catch (IOException e) {
            shutdown();
        }
    }
    public void shutdown() { // method that is called when client is disconnecting.
        done = true;
        try {
            in.close();
            out.close();
            if(!client.isClosed()) {
                client.close();
            }
        } catch (IOException e) {
            // ignore
        }
    }

    class InputHandler implements Runnable { // Handels input (messages) from a client.

        @Override
        public void run() { // Method that is called when a client is sending a message
            try {
                BufferedReader inReader = new BufferedReader(new InputStreamReader(System.in)); //Creates a buffering character-input stream that uses a default-sized input buffer.
                while (!done) {
                    String message = inReader.readLine();
                    if (message.equals("/quit")) {
                        out.println(message);
                        inReader.close();
                        shutdown();
                    }else {
                        out.println(message);
                    }
                }
            } catch (IOException e) {
                shutdown();
            }
        }
    }

    public static void main(String[] args) {
        Client client = new Client();
        client.run();
    }
}
