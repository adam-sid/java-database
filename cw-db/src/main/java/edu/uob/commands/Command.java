package edu.uob.commands;

import java.io.IOException;
import java.util.List;

public interface Command {

     List<String> execute() throws IOException;

}
