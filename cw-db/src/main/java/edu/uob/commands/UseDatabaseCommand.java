package edu.uob.commands;

import edu.uob.DatabaseContext;

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
    public List<String> execute() {
        File targetFile = new File(Paths.get(databaseContext.getDatabasesHome() +
                File.separator + databaseName ).toAbsolutePath().toString());
        if(!targetFile.exists() || !targetFile.isDirectory()) {
            throw new RuntimeException("No such database: " + databaseName);
        }
        databaseContext.setDatabaseName(databaseName);
        return List.of();
    }
}
