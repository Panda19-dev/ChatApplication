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

        String groupName = args[0]; // Get the group name from args
        server.joinGroup(groupName, invokingHandler); // Call the joinGroup method
    }
}