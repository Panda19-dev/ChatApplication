package Commands;

import BPs.Command;
import Exceptions.InvalidCommandArgumentException;
import Handlers.ConnectionHandler;

public class KickCommand extends Command {
    public KickCommand(ConnectionHandler ch) {
        super("Kick");
    }

    @Override
    public void execute(String[] args) throws InvalidCommandArgumentException {

    }
}
