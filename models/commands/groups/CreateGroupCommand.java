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
        server.createGroup(groupName, invokingHandler);
        invokingHandler.sendMessage("Group '" + groupName + "' created successfully.");
    }
}