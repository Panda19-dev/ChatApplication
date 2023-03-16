package BPs;

import Exceptions.InvalidCommandArgumentException;

public abstract class Command { //Abstract due to the abstract method
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
    public abstract void execute(String[] args) throws InvalidCommandArgumentException;
}