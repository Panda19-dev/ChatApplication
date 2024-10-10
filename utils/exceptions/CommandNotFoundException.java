package utils.exceptions;

import java.io.Serial;

public class CommandNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L; // Just needed to exist for a exception

    public CommandNotFoundException() { // Constructor
        super("Command not found.");
    }

    // Define a constructor with a message argument that sets the exception message to the provided message
    public CommandNotFoundException(String message) {
        super(message);
    }

    // Override the toString() method to return a string representation of the exception
    @Override
    public String toString() {
        return "CommandNotFoundException: " + getMessage();
    }
}
