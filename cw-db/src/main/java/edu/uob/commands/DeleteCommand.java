package edu.uob.commands;

import edu.uob.DatabaseContext;
import edu.uob.expression.Expression;

import java.io.IOException;
import java.util.List;

public class DeleteCommand implements Command {
    public DeleteCommand(DatabaseContext databaseContext, String tableName, Expression condition) {
    }

    @Override
    public List<String> execute() throws IOException {
        return List.of();
    }
}
