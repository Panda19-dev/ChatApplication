package Exceptions;

import java.io.Serial;

public class InvalidCommandArgumentException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public InvalidCommandArgumentException() {
        super("Invalid command argument.");
    }

    public InvalidCommandArgumentException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "InvalidCommandArgumentException: " + getMessage();
    }
}