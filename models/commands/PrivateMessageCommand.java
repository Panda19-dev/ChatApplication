package models.commands;

import models.Command;
import models.handlers.ConnectionHandler;
import server.Server;
import utils.exceptions.InvalidCommandArgumentException;

import java.util.Arrays;

public class PrivateMessageCommand extends Command {

    private final Server server;

    public PrivateMessageCommand(Server server) {
        super("pm");
        this.server = server;
        this.usage = "/pm <nickname> <message>";
        this.desc = "Sends a private message to a user.";
    }

    @Override
    public void execute(String[] args, ConnectionHandler invokingHandler) throws InvalidCommandArgumentException {
        if (args.length < 2) {
            throw new InvalidCommandArgumentException("Usage: /pm <nickname> <message>");
        }

        String targetNickname = args[0];
        String message = String.join(" ", Arrays.copyOfRange(args, 1, args.length));

        ConnectionHandler targetHandler = server.findHandlerByNickname(targetNickname);
        if (targetHandler == null) {
            throw new InvalidCommandArgumentException("User not found: " + targetNickname);
        }

        targetHandler.sendMessage("PM from " + invokingHandler.getNickname() + ": " + message);
        invokingHandler.sendMessage("You: " + message + " (to " + targetNickname + ")");
    }
}