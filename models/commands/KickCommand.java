package models.commands;

import models.Command;
import models.handlers.ConnectionHandler;
import server.Server;
import utils.exceptions.InvalidCommandArgumentException;

public class KickCommand extends Command {

    private final Server server;

    public KickCommand(Server server) {
        super("kick");
        this.server = server;
        this.usage = "/kick <nickname>";
        this.desc = "Kicks a specific person from the chat.";
    }

    @Override
    public void execute(String[] args, ConnectionHandler invokingHandler) throws InvalidCommandArgumentException {
        if (args.length < 1) {
            throw new InvalidCommandArgumentException("Usage: /kick <nickname>");
        }
        String nickname = args[0];
        if (nickname == null || nickname.isEmpty()) {
            throw new InvalidCommandArgumentException("Nickname cannot be empty.");
        }
        for (ConnectionHandler ch : server.getConnections()) {
            if (ch != null && ch.getNickname().equals(nickname)) {
                ch.sendMessage("You have been kicked!");
                ch.kick();
                server.removeHandler(ch); // Ensure the kicked handler is removed from the server
                return;
            }
        }
        throw new InvalidCommandArgumentException("User not found: " + nickname);
    }
}
