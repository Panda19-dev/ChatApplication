package models.commands.groups;

import models.Command;
import models.handlers.ConnectionHandler;
import server.Server;
import utils.exceptions.InvalidCommandArgumentException;

public class CreateGroupCommand extends Command {
    private final Server server;

    public CreateGroupCommand(Server server) {
        super("creategroup");
        this.server = server;
        this.desc = "Creates a new group with a specified name.";
        this.usage = "/creategroup [group_name]";
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

        // Attempt to create the group
        if (!server.createGroup(groupName, invokingHandler)) {
            invokingHandler.sendMessage("Server: Group creation failed. The group may already exist.");
        } else {
            invokingHandler.sendMessage("Server: Group '" + groupName + "' created successfully.");
        }
    }
}