package edu.uob;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public interface Command {

     List<String> execute() throws IOException;

}
