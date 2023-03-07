package Commands;

import Handlers.ConnectionHandler;
import BPs.Command;
import Exceptions.InvalidCommandArgumentException;

public class NickCommand extends Command {

    private ConnectionHandler connectionHandler;

    public NickCommand(ConnectionHandler connectionHandler) {
        super("nick");
        this.connectionHandler = connectionHandler;
    }


    // Override the execute() method of the Command class to handle executing the Nick command
    @Override
    public void execute(String[] args) throws InvalidCommandArgumentException {

        if (args.length != 1) {
            throw new InvalidCommandArgumentException("Usage: /nick <nickname>");
        }
        String newNickname = args[0];
        String oldNickname = connectionHandler.getNickname();
        connectionHandler.setNickname(newNickname);
        connectionHandler.broadcast(oldNickname + " changed nickname to " + newNickname, "SERVER");
        connectionHandler.sendMessage("Successfully changed nickname to " + newNickname);

    }
}
