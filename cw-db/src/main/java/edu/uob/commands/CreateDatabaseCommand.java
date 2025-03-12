package edu.uob.commands;

import edu.uob.DatabaseContext;

import java.io.File;
import java.util.List;

public class CreateDatabaseCommand implements Command{

    private final String databaseName;

    private final DatabaseContext databaseContext;

    public CreateDatabaseCommand(DatabaseContext databaseContext, String databaseName) {
        this.databaseName = databaseName;
        this.databaseContext = databaseContext;
    }

    public String getDatabaseName() {
        return databaseName;
    }
    //TODO throw an io exception
    public void deleteDatabase() {
        File databasesHomeFolder = new File(databaseContext.getDatabasesHome());
        File file = new File(databasesHomeFolder, databaseName);
        file.delete();
    }
    //TODO throw an io exception
    @Override
    public List<String> execute() {
        File databasesHomeFolder = new File(databaseContext.getDatabasesHome());
        File file = new File(databasesHomeFolder, databaseName);
        if (file.exists()) {
            throw new RuntimeException("Database " + databaseName + " already exists");
        }
        file.mkdirs();
        return List.of();
    }
}
