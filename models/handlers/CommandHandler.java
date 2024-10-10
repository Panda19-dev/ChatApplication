package models.handlers;

import models.Command;
import utils.exceptions.CommandNotFoundException;
import utils.exceptions.InvalidCommandArgumentException;

import java.util.HashMap;

public class CommandHandler {

    private static CommandHandler instance = null;
    private final HashMap<String, Command> commands;

    public CommandHandler() {
        // Initialize the HashMap to store commands
        this.commands = new HashMap<>();
    }

    public static CommandHandler getInstance() {
        if (instance == null) {
            instance = new CommandHandler();
        }
        return instance;
    }

    public void addCommand(Command command) {
        // Add the command to the HashMap, with the command name as the key and the Command object as the value
        this.commands.put(command.getName(), command);
    }

    // Add the missing getCommands method
    public HashMap<String, Command> getCommands() {
        return this.commands;
    }

    // In CommandHandler.java, use custom exceptions consistently
    public void executeCommand(String commandName, String[] args, ConnectionHandler invokingHandler)
            throws CommandNotFoundException, InvalidCommandArgumentException {
        Command command = commands.get(commandName);
        if (command == null) {
            throw new CommandNotFoundException("Command not found: " + commandName);
        }
        command.execute(args, invokingHandler); // Pass the invoking ConnectionHandler to the command
    }
}