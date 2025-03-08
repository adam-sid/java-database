package edu.uob.commands;

import java.util.ArrayList;
import java.util.List;

public class AddCommand implements Command {

    private final ArrayList<String> attributes;

    public AddCommand(ArrayList<String> attributes) {
        this.attributes = attributes;
    }

    @Override
    public List<String> execute() {
        return List.of();
    }
}
