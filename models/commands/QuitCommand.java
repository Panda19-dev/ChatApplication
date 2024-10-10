package models.commands;

import models.Command;
import models.handlers.ConnectionHandler;
import utils.exceptions.InvalidCommandArgumentException;

public class QuitCommand extends Command {

    public QuitCommand() {
        super("quit");
        this.usage = "/quit";
        this.desc = "Disconnects from the chat.";
    }

    @Override
    public void execute(String[] args, ConnectionHandler invokingHandler) throws InvalidCommandArgumentException {
        invokingHandler.sendMessage("You have disconnected from the chat.");
        invokingHandler.shutdown(); // Call the shutdown method to disconnect the client
    }
}