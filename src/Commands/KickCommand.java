package Commands;

import BPs.Command;
import Exceptions.InvalidCommandArgumentException;
import Handlers.ConnectionHandler;
import Main.Server;

public class KickCommand extends Command {

    private ConnectionHandler ch;
    private Server server;
    public KickCommand(ConnectionHandler ch, Server server) {
        super("Kick");
        this.ch = ch;
        this.server = server;
    }

    @Override
    public void execute(String[] args) throws InvalidCommandArgumentException {

    }
}
