package edu.uob;

import java.io.File;
import java.util.List;

public class UseDatabaseCommand implements Command {

    private final String databaseName;

    private final DatabaseContext databaseContext;

    public UseDatabaseCommand(DatabaseContext databaseContext, String databaseName) {
        this.databaseName = databaseName;
        this.databaseContext = databaseContext;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public List<String> execute() {
        String databaseName = getDatabaseName();
        databaseContext = new DatabaseContext(databaseName);
        return List.of();
    }
}
