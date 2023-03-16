package Commands;

import BPs.Command;
import Exceptions.InvalidCommandArgumentException;
import Handlers.CommandHandler;
import Handlers.ConnectionHandler;
import Main.Server;

import java.util.Arrays;
import java.util.HashMap;

public class HelpCommand extends Command {

    private Server server;
    private CommandHandler commandHandler;

    private ConnectionHandler ch;

    public HelpCommand(Server server, CommandHandler commandHandler, ConnectionHandler ch) {
        super("help");
        this.server = server;
        this.commandHandler = commandHandler;
        this.desc = "A help command!";
        this.usage = "/Help";
        this.ch = ch;
    }

    @Override
    public void execute(String[] args) throws InvalidCommandArgumentException {
        int i = 0;
         for (Command command : commandHandler.getCommands().values()) {
             i++;
             ch.sendMessage(i + ". " + command.getName() + "\n" +
                     "Description : " + command.getDesc() + "\n" +
                     "Usage : " + command.getUsage());
         }
    }
}
