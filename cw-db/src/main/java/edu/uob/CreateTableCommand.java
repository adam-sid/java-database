package edu.uob;

import java.util.ArrayList;
import java.util.List;

public class CreateTableCommand implements Command {

    private final DatabaseContext databaseContext;

    private final String databaseName;

    private final String tableName;

    private final List<String> attributes;

    public CreateTableCommand(DatabaseContext databaseContext, String databaseName, String tableName,
                              ArrayList<String> attributes) {
        this.databaseContext = databaseContext;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.attributes = attributes;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    @Override
    public List<String> execute() {
        Table newTable = new Table(tableName, attributes);
        return List.of();
    }
}
