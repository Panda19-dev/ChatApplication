package models.commands;

import models.Command;
import models.handlers.CommandHandler;
import models.handlers.ConnectionHandler;
import utils.exceptions.InvalidCommandArgumentException;

public class HelpCommand extends Command {
    private final CommandHandler commandHandler;

    public HelpCommand(CommandHandler commandHandler) {
        super("help");
        this.commandHandler = commandHandler;
        this.desc = "A help command!";
        this.usage = "/Help";
    }

    @Override
    public void execute(String[] args, ConnectionHandler invokingHandler) throws InvalidCommandArgumentException {
        int i = 0;
        for (Command command : commandHandler.getCommands().values()) {
            i++;
            invokingHandler.sendMessage(i + ". " + command.getName() + "\n" +
                    "Description : " + command.getDesc() + "\n" +
                    "Usage : " + command.getUsage());
        }
    }
}
