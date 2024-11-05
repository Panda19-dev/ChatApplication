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
        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Usage: " + usage);
        }

        String groupName = args[0];

        // Attempt to leave the group
        if (!server.leaveGroup(groupName, invokingHandler)) {
            invokingHandler.sendMessage("Server: Unable to leave the group. Please ensure the group exists and that you are a member.");
        } else {
            invokingHandler.sendMessage("Server: Successfully left the group '" + groupName + "'.");
        }
    }
}