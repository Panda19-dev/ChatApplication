package Exceptions;

import java.io.Serial;

public class InvalidCommandArgumentException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidCommandArgumentException() {
        super("Invalid command argument.");
    }

    // Define a constructor with a message argument that sets the exception message to the provided message
    public InvalidCommandArgumentException(String message) {
        super(message);
    }

    // Override the toString() method to return a string representation of the exception
    @Override
    public String toString() {
        return "InvalidCommandArgumentException: " + getMessage();
    }
}