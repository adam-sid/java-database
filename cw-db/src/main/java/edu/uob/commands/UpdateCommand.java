package edu.uob.commands;

import edu.uob.DatabaseContext;
import edu.uob.expression.Expression;

import java.io.IOException;
import java.util.List;

public class UpdateCommand implements Command {
    public UpdateCommand(DatabaseContext databaseContext, String tableName, Object p2, Object p3, Expression condition) {
    }

    @Override
    public List<String> execute() throws IOException {
        return List.of();
    }
}
