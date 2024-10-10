package models.commands;

import models.Command;
import models.handlers.ConnectionHandler;
import utils.exceptions.InvalidCommandArgumentException;

public class NickCommand extends Command {

    public NickCommand() {
        super("nick");
        this.usage = "/Nick <nickname>";
        this.desc = "Changes your nickname";
    }

    @Override
    public void execute(String[] args, ConnectionHandler invokingHandler) throws InvalidCommandArgumentException {

        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Usage: /nick <nickname>");
        }
        String newNickname = args[0];
        String oldNickname = invokingHandler.getNickname();

        if (newNickname == null || newNickname.isEmpty()) {
            throw new InvalidCommandArgumentException("Nickname cannot be empty.");
        }

        invokingHandler.setNickname(newNickname);
        invokingHandler.broadcastAsServer(oldNickname + " changed nickname to " + newNickname);
        invokingHandler.sendMessage("Successfully changed nickname to " + newNickname);
    }
}
