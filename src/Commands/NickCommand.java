package Commands;

import BPs.Command;
import Exceptions.InvalidCommandArgumentException;

public class NickCommand extends Command {

    public NickCommand() {
        super("Nick");
    }

    @Override
    public void execute(String[] args) throws InvalidCommandArgumentException {

    }
}
