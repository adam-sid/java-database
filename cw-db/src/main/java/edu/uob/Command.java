package edu.uob;

import java.util.ArrayList;

public class Command {

    private final ArrayList<String> commandArr;

    public Command(String command) {
        this.commandArr = BasicTokeniser.setup(command);
    }

    public ArrayList<String> getCommand () {
        return commandArr;
    }

    public String getToken (int tokenIndex) {
        return commandArr.get(tokenIndex);
    }
}
