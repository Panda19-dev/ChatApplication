package Exceptions;

import java.io.Serial;

public class CommandNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public CommandNotFoundException() {
        super("Command not found.");
    }

    public CommandNotFoundException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "CommandNotFoundException: " + getMessage();
    }
}