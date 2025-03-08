package edu.uob.commands;

import edu.uob.DatabaseContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class DropDatabaseCommand implements Command {

    private final String databaseName;

    private final DatabaseContext databaseContext;

    public DropDatabaseCommand(DatabaseContext databaseContext, String databaseName) {
        this.databaseName = databaseName;
        this.databaseContext = databaseContext;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    @Override
    public List<String> execute() throws IOException {
        File targetFile = new File(Paths.get(databaseContext.getDatabasesHome() + File.separator +
                databaseName).toAbsolutePath().toString());
        if(!targetFile.exists() || !targetFile.isDirectory()) {
            throw new RuntimeException("No such database: " + databaseName);
        }
        try {
            File[] databaseFiles = targetFile.listFiles();

            if(databaseFiles == null || databaseFiles.length == 0) {
                targetFile.delete();
            } else {
                Arrays.stream(databaseFiles).forEach(file -> {
                    file.delete();
                });
                targetFile.delete();
            }
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not delete database: " + databaseName);
        }
        return List.of();
    }
}
