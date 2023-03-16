package Commands;

import BPs.Command;
import Exceptions.InvalidCommandArgumentException;
import Handlers.ConnectionHandler;
import Main.Server;

public class KickCommand extends Command {
    private Server server;
    public KickCommand(Server server) {
        super("kick");
        this.server = server;
        this.usage = "/kick <nickname>";
        this.desc = "Kicks a specific person from the chat.";
    }

    @Override
    public void execute(String[] args) throws InvalidCommandArgumentException {
        if (args.length < 1) {
            throw new InvalidCommandArgumentException("Usage: /kick <nickname>");
        }
        String nickname = args[0];
        for (ConnectionHandler ch : server.getConnections()) {
            if (ch != null && ch.getNickname().equals(nickname)) {
                ch.kick();
                server.removeConnection(ch);
                System.out.println(nickname + " has been kicked from the chat.");
                return;
            }
        }
        throw new InvalidCommandArgumentException("User not found: " + nickname);
    }
}

