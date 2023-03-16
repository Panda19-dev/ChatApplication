package Handlers;

import BPs.Command;
import Exceptions.CommandNotFoundException;
import Exceptions.InvalidCommandArgumentException;

import java.util.HashMap;

public class CommandHandler {

    private static CommandHandler instance = null;

    private HashMap<String, Command> commands;

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

    public void executeCommand(String commandName, String[] args) throws CommandNotFoundException, InvalidCommandArgumentException {

        // Get the Command object from the HashMap using the command name as the key
        Command command = this.commands.get(commandName.toLowerCase());

        // If the Command object is not null (i.e., the command exists), execute the command with the provided arguments
        if (command != null) {
            command.execute(args);
        } else {
            // If the Command object is null (i.e., the command does not exist), throw a CommandNotFoundException
            throw new CommandNotFoundException("Command not found.");
        }
    }
    public HashMap<String, Command> getCommands() {
        return commands;
    }
}