package edu.uob.commands;

import edu.uob.DatabaseContext;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class DropTableCommand implements Command {

    private final DatabaseContext databaseContext;

    private final String tableName;

    public DropTableCommand(DatabaseContext databaseContext, String tableName) {
        this.databaseContext = databaseContext;
        this.tableName = tableName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public List<String> execute() throws IOException {
        File targetFile = new File(Paths.get(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName(), tableName + ".tab").toAbsolutePath().toString());
        if(!targetFile.exists() || !targetFile.isFile()) {
            throw new RuntimeException("No such table: " + tableName);
        }
        try {
            targetFile.delete();
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not delete file: " + tableName);
        }
        return List.of();
    }
}
