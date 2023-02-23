package BPs;

import Exceptions.InvalidCommandArgumentException;

public abstract class Command {
    private String name;

    public Command(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    // Define an abstract method to execute the command, which must be implemented in the subclasses
    public abstract void execute(String[] args) throws InvalidCommandArgumentException;
}