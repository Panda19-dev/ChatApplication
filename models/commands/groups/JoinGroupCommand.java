package models.commands.groups;

import models.Command;
import models.handlers.ConnectionHandler;
import server.Server;
import utils.exceptions.InvalidCommandArgumentException;

public class JoinGroupCommand extends Command {
    private final Server server;

    public JoinGroupCommand(Server server) {
        super("joingroup");
        this.server = server;
        this.desc = "Joins a group.";
        this.usage = "/joingroup <groupName>";
    }

    @Override
    public void execute(String[] args, ConnectionHandler invokingHandler) throws InvalidCommandArgumentException {
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Usage: " + usage);
        }

        String groupName = args[0];

        // Validate the group name (e.g., ensure it's not empty and follows some criteria)
        if (groupName.isEmpty() || groupName.length() > 30 || !groupName.matches("[a-zA-Z0-9_-]+")) {
            throw new InvalidCommandArgumentException("Invalid group name. Group names must be alphanumeric and can contain underscores or dashes, with a max length of 30 characters.");
        }

        // Attempt to join the group
        if (!server.joinGroup(groupName, invokingHandler)) {
            invokingHandler.sendMessage("Server: Failed to join group. The group may not exist, or you are already a member.");
        } else {
            invokingHandler.sendMessage("Server: Successfully joined the group '" + groupName + "'.");
        }
    }
}