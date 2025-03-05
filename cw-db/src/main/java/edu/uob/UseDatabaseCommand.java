package edu.uob;

import java.io.File;
import java.nio.file.Paths;
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
    //TODO check if database exists
    public List<String> execute() {
        databaseContext.setDatabaseName(databaseName);
        File targetFile = new File(Paths.get(databaseContext.getDatabasesHome() +
                File.separator + databaseName ).toAbsolutePath().toString());
        if(!targetFile.exists() || !targetFile.isDirectory()) {
            throw new RuntimeException("No such database: " + databaseName);
        }
        return List.of();
    }
}
