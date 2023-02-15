package Handlers;

import BPs.Command;
import Exceptions.CommandNotFoundException;
import Exceptions.InvalidCommandArgumentException;

import java.util.HashMap;

public class CommandHandler {

    private static CommandHandler instance = null;

    private HashMap<String, Command> commands;

    private CommandHandler() {
        this.commands = new HashMap<>();
    }

    public static CommandHandler getInstance() {
        if (instance == null) {
            instance = new CommandHandler();
        }
        return instance;
    }

    public void addCommand(Command command) {
        this.commands.put(command.getName(), command);
    }

    public void executeCommand(String commandName, String[] args) throws CommandNotFoundException, InvalidCommandArgumentException {
        Command command = this.commands.get(commandName);
        if (command != null) {
            command.execute(args);
        } else {
            throw new CommandNotFoundException("Command not found.");
        }
    }
}