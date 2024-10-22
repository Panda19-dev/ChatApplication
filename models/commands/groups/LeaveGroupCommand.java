package models.commands.groups;

import models.Command;
import models.handlers.ConnectionHandler;
import server.Server;
import utils.exceptions.InvalidCommandArgumentException;

public class LeaveGroupCommand extends Command {
    private final Server server;

    public LeaveGroupCommand(Server server) {
        super("leavegroup");
        this.server = server;
        this.desc = "Leaves the current group.";
        this.usage = "/leavegroup";
    }

    @Override
    public void execute(String[] args, ConnectionHandler invokingHandler) throws InvalidCommandArgumentException {
        if (args.length != 1) { // Expecting one argument: the group name
            throw new InvalidCommandArgumentException("Usage: " + usage);
        }

        String groupName = args[0]; // Get the group name from args
        server.leaveGroup(groupName, invokingHandler); // Pass both groupName and invokingHandler
    }
}