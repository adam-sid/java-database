package edu.uob;

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

    @Override
    public List<String> execute() {
        File databasesHomeFolder = new File(databaseContext.getDatabasesHome());
        File file = new File(databasesHomeFolder, databaseName);
        file.mkdirs();
        return List.of();
    }
}
