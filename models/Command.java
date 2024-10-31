package models;

import models.handlers.ConnectionHandler;
import utils.exceptions.InvalidCommandArgumentException;

public abstract class Command {
    private String name;
    protected String desc;
    protected String usage;

    public Command(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public String getDesc() { return this.desc; }
    public String getUsage() { return this.usage; }

    // Define an abstract method to execute the command, which must be implemented in the subclasses
    public abstract void execute(String[] args, ConnectionHandler invokingHandler) throws InvalidCommandArgumentException;
}
